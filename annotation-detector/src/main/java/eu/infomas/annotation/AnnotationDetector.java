/* AnnotationDetector.java
 *
 * Created: 2011-10-10 (Year-Month-Day)
 * Character encoding: UTF-8
 *
 ****************************************** LICENSE *******************************************
 *
 * Copyright (c) 2011 - 2014 XIAM Solutions B.V. (http://www.xiam.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.infomas.annotation;

import java.io.DataInput;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@code AnnotationDetector} reads Java Class Files ("*.class") and reports the
 * found annotations via a simple, developer friendly API.
 * <p>
 * A Java Class File consists of a stream of 8-bit bytes. All 16-bit, 32-bit, and 64-bit
 * quantities are constructed by reading in two, four, and eight consecutive 8-bit
 * bytes, respectively. Multi byte data items are always stored in big-endian order,
 * where the high bytes come first. In the Java platforms, this format is
 * supported by interfaces {@link java.io.DataInput} and {@link java.io.DataOutput}.
 * <p>
 * A class file consists of a single ClassFile structure:
 * <pre>
 * ClassFile {
 *   u4 magic;
 *   u2 minor_version;
 *   u2 major_version;
 *   u2 constant_pool_count;
 *   cp_info constant_pool[constant_pool_count-1];
 *   u2 access_flags;
 *   u2 this_class;
 *   u2 super_class;
 *   u2 interfaces_count;
 *   u2 interfaces[interfaces_count];
 *   u2 fields_count;
 *   field_info fields[fields_count];
 *   u2 methods_count;
 *   method_info methods[methods_count];
 *   u2 attributes_count;
 *   attribute_info attributes[attributes_count];
 * }
 *
 * Where:
 * u1 unsigned byte {@link java.io.DataInput#readUnsignedByte()}
 * u2 unsigned short {@link java.io.DataInput#readUnsignedShort()}
 * u4 unsigned int {@link java.io.DataInput#readInt()}
 *
 * Annotations are stored as Attributes, named "RuntimeVisibleAnnotations" for
 * {@link java.lang.annotation.RetentionPolicy#RUNTIME} and "RuntimeInvisibleAnnotations" for
 * {@link java.lang.annotation.RetentionPolicy#CLASS}.
 * </pre>
 * References:
 * <ul>
 * <li><a href="http://en.wikipedia.org/wiki/Java_class_file">Java class file (Wikipedia)</a>
 * (Gentle Introduction);
 * <li><a href="http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html">Java
 * VM Specification, Java SE 8 Edition (Chapter 4)</a> for the real work.
 * <li><a href="http://stackoverflow.com/questions/259140">scanning java annotations at
 * runtime</a>.
 * </ul>
 * <p>
 * Similar projects / libraries:
 * <ul>
 * <li><a href="http://community.jboss.org/wiki/MCScanninglib">JBoss MC Scanning lib</a>;
 * <li><a href="http://code.google.com/p/reflections/">Google Reflections</a>, in fact an
 * improved version of <a href="http://scannotation.sourceforge.net/">scannotation</a>;
 * <li><a href="https://github.com/ngocdaothanh/annovention">annovention</a>, improved version
 * of the <a href="http://code.google.com/p/annovention">original Annovention</a> project.
 * Available from maven: {@code tv.cntt:annovention:1.2};
 * <li>If using the Spring Framework, use {@code ClassPathScanningCandidateComponentProvider}
 * </ul>
 * <p>
 * All above mentioned projects make use of a byte code manipulation library (like BCEL,
 * ASM or Javassist).
 *
 * @author <a href="mailto:rmuller@xiam.nl">Ronald K. Muller</a>
 * @since annotation-detector 3.0.0
 */
public final class AnnotationDetector implements Builder, Cursor {

    private static final Logger LOG = Logger.getLogger(AnnotationDetector.class.getName());

    // Constant Pool type tags
    private static final int CP_UTF8 = 1;
    private static final int CP_INTEGER = 3;
    private static final int CP_FLOAT = 4;
    private static final int CP_LONG = 5;
    private static final int CP_DOUBLE = 6;
    private static final int CP_CLASS = 7;
    private static final int CP_STRING = 8;
    private static final int CP_REF_FIELD = 9;
    private static final int CP_REF_METHOD = 10;
    private static final int CP_REF_INTERFACE = 11;
    private static final int CP_NAME_AND_TYPE = 12;
    private static final int CP_METHOD_HANDLE = 15; // Java VM SE 7
    private static final int CP_METHOD_TYPE = 16; // Java VM SE 7
    private static final int CP_INVOKE_DYNAMIC = 18; // Java VM SE 7

    // AnnotationElementValue / Java raw types
    private static final int BYTE = 'B';
    private static final int CHAR = 'C';
    private static final int DOUBLE = 'D';
    private static final int FLOAT = 'F';
    private static final int INT = 'I';
    private static final int LONG = 'J';
    private static final int SHORT = 'S';
    private static final int BOOLEAN = 'Z';
    private static final int ARRAY = '[';
    // used for AnnotationElement only
    private static final int STRING = 's';
    private static final int ENUM = 'e';
    private static final int CLASS = 'c';
    private static final int ANNOTATION = '@';

    // The buffer is reused during the life cycle of this AnnotationDetector instance
    private final ClassFileBuffer cpBuffer = new ClassFileBuffer();
    private final ClassFileIterator cfIterator;
    // The Element Types to detect
    private final Set<ElementType> elementTypes = EnumSet.of(ElementType.TYPE);
    // Reusing the constantPool is not needed for better performance
    private Object[] constantPool;

    // The cached annotation types to report, maps raw Annotation type name to Class object
    private Map<String, Class<? extends Annotation>> annotations;
    private FilenameFilter filter;
    private Reporter reporter;

    // The current annotation reported
    private Class<? extends Annotation> annotationType;
    // The 'raw' name of the current interface or class being scanned and reported
    // (using '/' instead of '.' in package name)
    private String typeName;
    // The current method or field (if any) being scanned
    private String memberName;
    // The current Element Type beinig reported
    private ElementType elementType;
    // The method descriptor of the currently reported annotated method in "raw"
    // format (as it appears in the Java Class File). Example method descriptors:
    // "()V"                      no arguments, return type void
    // "(Ljava/lang/String;II)I"  String, int, int as arguments, return type int
    private String methodDescriptor;

    private AnnotationDetector(final File[] filesOrDirectories, final String[] pkgNameFilter) {
        this.cfIterator = new ClassFileIterator(filesOrDirectories, pkgNameFilter);
        if (filesOrDirectories.length == 0) {
            LOG.warning("No files or directories to scan!");
        } else if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Files and root directories scanned:\n{0}",
                Arrays.toString(filesOrDirectories).replace(", ", "\n"));
        }
    }

    /**
     * Factory method, starting point for the fluent interface.
     * Only scan Class Files in the specified packages. If nothing is specified, all classes
     * on the class path are scanned.
     */
    public static Builder scanClassPath(final String... packageNames)
        throws IOException {

        final Set<File> files = new HashSet<File>();
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final String[] pkgNameFilter;
        if (packageNames.length == 0) {
            pkgNameFilter = null;
            final String[] fileNames = System.getProperty("java.class.path")
                .split(File.pathSeparator);
            for (int i = 0; i < fileNames.length; ++i) {
                files.add(new File(fileNames[i]));
            }
        } else {
            pkgNameFilter = new String[packageNames.length];
            for (int i = 0; i < pkgNameFilter.length; ++i) {
                pkgNameFilter[i] = packageNames[i].replace('.', '/');
                if (!pkgNameFilter[i].endsWith("/")) {
                    pkgNameFilter[i] = pkgNameFilter[i].concat("/");
                }
            }
            for (final String packageName : pkgNameFilter) {
                addFiles(loader, packageName, files);
            }
        }
        return new AnnotationDetector(files.toArray(new File[files.size()]), pkgNameFilter);
    }

    /**
     * Factory method, starting point for the fluent interface.
     * Scan all files it the specified jar files and directories.
     */
    public static Builder scanFiles(final File... filesOrDirectories) {
        return new AnnotationDetector(filesOrDirectories, null);
    }

    /**
     * See {@link Builder#forAnnotations(java.lang.Class...) }.
     */
    @Override
    public Builder forAnnotations(final Class<? extends Annotation>... a) {
        annotations = new HashMap<String, Class<? extends Annotation>>(a.length);
        // map "raw" type names to Class object
        for (int i = 0; i < a.length; ++i) {
            annotations.put("L" + a[i].getName().replace('.', '/') + ";", a[i]);
        }
        return this;
    }

    /**
     * See {@link Builder#on(java.lang.annotation.ElementType...)  }.
     */
    @Override
    public Builder on(final ElementType... types) {
        if (types.length == 0) {
            throw new IllegalArgumentException("At least one Element Type must be specified");
        }
        elementTypes.clear();
        for (ElementType t : types) {
            switch (t) {
                case TYPE:
                case CONSTRUCTOR:
                case METHOD:
                case FIELD:
                    elementTypes.add(t);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported: " + t);
            }
        }
        return this;
    }

    /**
     * See {@link Builder#filter(java.io.FilenameFilter) }.
     */
    @Override
    public Builder filter(final FilenameFilter filter) {
        if (filter == null) {
            throw new NullPointerException("'filter' may not be null");
        }
        this.filter = filter;
        return this;
    }

    /**
     * See {@link Builder#report(eu.infomas.annotation.AnnotationDetector.Reporter) }.
     */
    @Override
    public void report(final Reporter reporter) throws IOException {
        this.reporter = reporter;
        detect(cfIterator);
    }

    /**
     * See {@link Builder#collect(eu.infomas.annotation.AnnotationDetector.ReporterFunction) }.
     */
    @Override
    public <T> List<T> collect(final ReporterFunction<T> reporter) throws IOException {
        final List<T> list = new ArrayList<T>();
        this.reporter = new Reporter() {

            @Override
            public void report(Cursor cursor) {
                list.add(reporter.report(cursor));
            }

        };
        detect(cfIterator);
        return list;
    }

    /**
     * See {@link Cursor#getTypeName() }.
     */
    @Override
    public String getTypeName() {
        return typeName.replace('/', '.');
    }

    /**
     * See {@link Cursor#getAnnotationType() }.
     */
    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return annotationType;
    }

    /**
     * See {@link Cursor#getElementType() }.
     */
    @Override
    public ElementType getElementType() {
        return elementType;
    }

    /**
     * See {@link Cursor#getMemberName() }.
     */
    @Override
    public String getMemberName() {
        return memberName;
    }

    /**
     * See {@link Cursor#getType() }.
     */
    @Override
    public Class<?> getType() {
        return loadClass(getTypeName());
    }

    /**
     * See {@link Cursor#getField() }.
     */
    @Override
    public Field getField() {
        if (elementType != ElementType.FIELD) {
            throw new IllegalStateException(
                "Illegal to call getField() when " + elementType + " is reported");
        }
        try {
            return getType().getDeclaredField(memberName);
        } catch (NoSuchFieldException ex) {
            throw assertionError(
                "Cannot find Field '%s' for type %s", memberName, getTypeName());
        }
    }

    /**
     * See {@link Cursor#getConstructor() }.
     */
    @Override
    public Constructor getConstructor() {
        if (elementType != ElementType.CONSTRUCTOR) {
            throw new IllegalStateException(
                "Illegal to call getMethod() when " + elementType + " is reported");
        }
        try {
            final Class<?>[] parameterTypes = parseArguments(methodDescriptor);
            return getType().getConstructor(parameterTypes);
        } catch (NoSuchMethodException ex) {
            throw assertionError(
                "Cannot find Contructor '%s(...)' for type %s", memberName, getTypeName());
        }
    }

    /**
     * See {@link Cursor#getMethod() }.
     */
    @Override
    public Method getMethod() {
        if (elementType != ElementType.METHOD) {
            throw new IllegalStateException(
                "Illegal to call getMethod() when " + elementType + " is reported");
        }
        try {
            final Class<?>[] parameterTypes = parseArguments(methodDescriptor);
            return getType().getDeclaredMethod(memberName, parameterTypes);
        } catch (NoSuchMethodException ex) {
            throw assertionError(
                "Cannot find Method '%s(...)' for type %s", memberName, getTypeName());
        }
    }

    /**
     * See {@link Cursor#getAnnotation(java.lang.Class) }.
     */
    @Override
    public <T extends Annotation> T getAnnotation(final Class<T> annotationClass) {
        if (!annotationClass.equals(annotationType)) {
            throw new IllegalStateException("Illegal to call getAnnotation() when " +
                this.annotationType.getName() + " is reported");
        }
        final AnnotatedElement ae;
        switch (elementType) {
            case TYPE:
                ae = getType();
                break;
            case FIELD:
                ae = getField();
                break;
            case METHOD:
                ae = getMethod();
                break;
            default:
                throw new AssertionError(elementType);
        }
        return ae.getAnnotation(annotationClass);
    }

    // private

    private static void addFiles(ClassLoader loader, String resourceName, Set<File> files)
        throws IOException {

        final Enumeration<URL> resourceEnum = loader.getResources(resourceName);
        while (resourceEnum.hasMoreElements()) {
            final URL url = resourceEnum.nextElement();
            LOG.log(Level.FINER, "Resource URL: {0}", url);
            // Handle JBoss VFS URL's which look like (example package 'nl.dvelop'):
            // vfs:/foo/bar/website.war/WEB-INF/classes/nl/dvelop/
            // vfs:/foo/bar/website.war/WEB-INF/lib/dwebcore-0.0.1.jar/nl/dvelop/
            final boolean isVfs = "vfs".equals(url.getProtocol());
            if ("file".equals(url.getProtocol()) || isVfs) {
                final File dir = toFile(url);
                if (dir.isDirectory()) {
                    files.add(dir);
                } else if (isVfs) {
                    //Jar file via JBoss VFS protocol - strip package name
                    String jarPath = dir.getPath();
                    final int idx = jarPath.indexOf(".jar");
                    if (idx > -1) {
                        jarPath = jarPath.substring(0, idx + 4);
                        final File jarFile = new File(jarPath);
                        if (jarFile.isFile()) {
                            files.add(jarFile);
                        }
                    }
                } else {
                    throw assertionError("Not a recognized file URL: %s", url);
                }
            } else {
                // Resource in Jar File
                final File jarFile =
                    toFile(((JarURLConnection)url.openConnection()).getJarFileURL());
                if (jarFile.isFile()) {
                    files.add(jarFile);
                } else {
                    throw assertionError("Not a File: %s", jarFile);
                }
            }
        }
    }

    private static File toFile(final URL url) throws IOException {
        // only correct way to convert the URL to a File object, also see issue #16
        // Do not use URLDecoder
        try {
            return new File(url.toURI());
        } catch (URISyntaxException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    @SuppressWarnings("illegalcatch")
    private void detect(final ClassFileIterator iterator) throws IOException {
        InputStream stream;
        while ((stream = iterator.next(filter)) != null) {
            try {
                cpBuffer.readFrom(stream);
                if (hasCafebabe(cpBuffer)) {
                    LOG.log(Level.FINE, "File: {0}", iterator.getName());
                    read(cpBuffer);
                } // else ignore
            } catch (RuntimeException t) {
                // catch all errors to savely close ZipFileIterator
                if (!iterator.isFile()) {
                    // in case of an error we close the ZIP File here
                    stream.close();
                }
                throw t;
            } finally {
                // closing InputStream from ZIP Entry is handled by ZipFileIterator
                if (iterator.isFile()) {
                    stream.close();
                }
            }
        }
    }

    private boolean hasCafebabe(final ClassFileBuffer buffer) throws IOException {
        return buffer.size() > 4 &&  buffer.readInt() == 0xCAFEBABE;
    }

    /**
     * Inspect the given (Java) class file in streaming mode.
     */
    private void read(final DataInput di) throws IOException {
        readVersion(di);
        readConstantPoolEntries(di);
        readAccessFlags(di);
        readThisClass(di);
        readSuperClass(di);
        readInterfaces(di);
        readFields(di);
        readMethods(di);
        readAttributes(di, ElementType.TYPE);
    }

    private void readVersion(final DataInput di) throws IOException {
        // sequence: minor version, major version (argument_index is 1-based)
        if (LOG.isLoggable(Level.FINER)) {
            LOG.log(Level.FINER, "Java Class version {1}.{0}",
                new Object[]{di.readUnsignedShort(), di.readUnsignedShort()});
        } else {
            di.skipBytes(4);
        }
    }

    private void readConstantPoolEntries(final DataInput di) throws IOException {
        final int count = di.readUnsignedShort();
        constantPool = new Object[count];
        for (int i = 1; i < count; ++i) {
            if (readConstantPoolEntry(di, i)) {
                // double slot
                ++i;
            }
        }
    }

    /**
     * Return {@code true} if a double slot is read (in case of Double or Long constant).
     */
    private boolean readConstantPoolEntry(final DataInput di, final int index)
        throws IOException {

        final int tag = di.readUnsignedByte();
        switch (tag) {
            case CP_METHOD_TYPE:
                di.skipBytes(2);  // readUnsignedShort()
                return false;
            case CP_METHOD_HANDLE:
                di.skipBytes(3);
                return false;
            case CP_INTEGER:
            case CP_FLOAT:
            case CP_REF_FIELD:
            case CP_REF_METHOD:
            case CP_REF_INTERFACE:
            case CP_NAME_AND_TYPE:
            case CP_INVOKE_DYNAMIC:
                di.skipBytes(4); // readInt() / readFloat() / readUnsignedShort() * 2
                return false;
            case CP_LONG:
            case CP_DOUBLE:
                di.skipBytes(8); // readLong() / readDouble()
                return true;
            case CP_UTF8:
                constantPool[index] = di.readUTF();
                return false;
            case CP_CLASS:
            case CP_STRING:
                // reference to CP_UTF8 entry. The referenced index can have a higher number!
                constantPool[index] = di.readUnsignedShort();
                return false;
            default:
                throw new ClassFormatError(
                    "Unkown tag value for constant pool entry: " + tag);
        }
    }

    private void readAccessFlags(final DataInput di) throws IOException {
        di.skipBytes(2); // u2
    }

    private void readThisClass(final DataInput di) throws IOException {
        typeName = resolveUtf8(di);
    }

    private void readSuperClass(final DataInput di) throws IOException {
        di.skipBytes(2); // u2
    }

    private void readInterfaces(final DataInput di) throws IOException {
        final int count = di.readUnsignedShort();
        di.skipBytes(count * 2); // count * u2
    }

    private void readFields(final DataInput di) throws IOException {
        final int count = di.readUnsignedShort();
        for (int i = 0; i < count; ++i) {
            readAccessFlags(di);
            memberName = resolveUtf8(di);
            // decriptor is Field type in raw format, we do not need it, so skip
            //final String descriptor = resolveUtf8(di);
            di.skipBytes(2);
            LOG.log(Level.FINER, "Field: {0}", memberName);
            readAttributes(di, ElementType.FIELD);
        }
    }

    private void readMethods(final DataInput di) throws IOException {
        final int count = di.readUnsignedShort();
        for (int i = 0; i < count; ++i) {
            readAccessFlags(di);
            memberName = resolveUtf8(di);
            methodDescriptor = resolveUtf8(di);
            LOG.log(Level.FINER, "Method: {0}", memberName);
            readAttributes(di, "<init>".equals(memberName) ? ElementType.CONSTRUCTOR : ElementType.METHOD);
        }
    }

    private void readAttributes(final DataInput di, final ElementType reporterType)
        throws IOException {

        final int count = di.readUnsignedShort();
        for (int i = 0; i < count; ++i) {
            final String name = resolveUtf8(di);
            // in bytes, use this to skip the attribute info block
            final int length = di.readInt();
            if (elementTypes.contains(reporterType) &&
                ("RuntimeVisibleAnnotations".equals(name) ||
                "RuntimeInvisibleAnnotations".equals(name))) {
                LOG.log(Level.FINEST, "Attribute: {0}", name);
                readAnnotations(di, reporterType);
            } else {
                LOG.log(Level.FINEST, "Attribute: {0} (ignored)", name);
                di.skipBytes(length);
            }
        }
    }

    private void readAnnotations(final DataInput di, final ElementType elementType)
        throws IOException {

        // the number of Runtime(In)VisibleAnnotations
        final int count = di.readUnsignedShort();
        for (int i = 0; i < count; ++i) {
            final String rawTypeName = readAnnotation(di);
            annotationType = annotations.get(rawTypeName);
            if (annotationType == null) {
                LOG.log(Level.FINER, "Annotation: {0} (ignored)", rawTypeName);
                continue;
            }
            LOG.log(Level.FINE,
                "Annotation: ''{0}'' on type ''{1}'', member ''{2}'' (reported)",
                new Object[]{annotationType.getName(), getTypeName(), getMemberName()});
            this.elementType = elementType;
            reporter.report(this);
        }
    }

    private String readAnnotation(final DataInput di) throws IOException {
        final String rawTypeName = resolveUtf8(di);
        // num_element_value_pairs
        final int count = di.readUnsignedShort();
        for (int i = 0; i < count; ++i) {
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.log(Level.FINEST, "Anntotation Element: {0}", resolveUtf8(di));
            } else {
                di.skipBytes(2);
            }
            readAnnotationElementValue(di);
        }
        return rawTypeName;
    }

    private void readAnnotationElementValue(final DataInput di) throws IOException {
        final int tag = di.readUnsignedByte();
        switch (tag) {
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
            case BOOLEAN:
            case STRING:
                di.skipBytes(2);
                break;
            case ENUM:
                di.skipBytes(4); // 2 * u2
                break;
            case CLASS:
                di.skipBytes(2);
                break;
            case ANNOTATION:
                readAnnotation(di);
                break;
            case ARRAY:
                final int count = di.readUnsignedShort();
                for (int i = 0; i < count; ++i) {
                    readAnnotationElementValue(di);
                }
                break;
            default:
                throw new ClassFormatError("Not a valid annotation element type tag: 0x" +
                    Integer.toHexString(tag));
        }
    }

    /**
     * Look up the String value, identified by the u2 index value from constant pool
     * (direct or indirect).
     */
    private String resolveUtf8(final DataInput di) throws IOException {
        final int index = di.readUnsignedShort();
        final Object value = constantPool[index];
        final String s;
        if (value instanceof Integer) {
            s = (String)constantPool[(Integer)value];
        } else {
            s = (String)value;
        }
        return s;
    }

    /**
     * Return the method arguments of the currently reported annotated method as a
     * {@code Class} array.
     */
    // incorrect detection of dereferencing possible null pointer
    // TODO: https://github.com/checkstyle/checkstyle/issues/14 fixed in 5.8?
    @SuppressWarnings({"null", "javancss" })
    private Class<?>[] parseArguments(final String descriptor) {
        final int n = descriptor.length();
        // "minimal" descriptor: no arguments: "()V", first character is always '('
        if (n < 3 || descriptor.charAt(0) != '(') {
            throw unparseable(descriptor, "Wrong format");
        }
        List<Class<?>> args = null;
        for (int i = 1; i < n; ++i) {
            char c = descriptor.charAt(i);
            if (i == 1) {
                if (c == ')') {
                    return new Class<?>[0];
                } else {
                    args = new LinkedList<Class<?>>();
                }
            }
            assert args != null;
            int j;
            switch (c) {
                case 'V':
                    args.add(void.class);
                    break;
                case 'Z':
                    args.add(boolean.class);
                    break;
                case 'C':
                    args.add(char.class);
                    break;
                case 'B':
                    args.add(byte.class);
                    break;
                case 'S':
                    args.add(short.class);
                    break;
                case 'I':
                    args.add(int.class);
                    break;
                case 'F':
                    args.add(float.class);
                    break;
                case 'J':
                    args.add(long.class);
                    break;
                case 'D':
                    args.add(double.class);
                    break;
                case '[':
                    j = i;
                    do {
                        c = descriptor.charAt(++j);
                    } while (c == '['); // multi dimensional array
                    if (c == 'L') {
                        j = descriptor.indexOf(';', i + 1);
                    }
                    args.add(loadClass(descriptor.substring(i, j + 1)));
                    i = j;
                    break;
                case 'L':
                    j = descriptor.indexOf(';', i + 1);
                    args.add(loadClass(descriptor.substring(i + 1, j)));
                    i = j;
                    break;
                case ')':
                    // end of argument type list, stop parsing
                    return args.toArray(new Class<?>[args.size()]);
                default:
                    throw unparseable(descriptor, "Not a recognoized type: " + c);
            }
        }
        throw unparseable(descriptor, "No closing parenthesis");
    }

    /**
     * Load the class, but do not initialize it.
     */
    private static Class<?> loadClass(final String rawClassName) {
        final String typeName = rawClassName.replace('/', '.');
        try {
            final ClassLoader cl = Thread.currentThread().getContextClassLoader();
            return Class.forName(typeName, false, cl);
        } catch (ClassNotFoundException ex) {
            throw assertionError(
                "Cannot load type '%s', scanned file not on class path? (%s)", typeName, ex);
        }
    }

    /**
     * The method descriptor must always be parseable, so if not an AssertionError is thrown.
     */
    private static AssertionError unparseable(final String descriptor, final String cause) {
        return assertionError(
            "Unparseble method descriptor: '%s' (cause: %s)", descriptor, cause);
    }

    private static AssertionError assertionError(String message, Object... args) {
        return new AssertionError(String.format(message, args));
    }

}
