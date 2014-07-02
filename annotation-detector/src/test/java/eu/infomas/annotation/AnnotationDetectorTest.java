package eu.infomas.annotation;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

@RuntimeVisibleTestAnnotation(name = "On class level")
@RuntimeInvisibleTestAnnotation
// This annotation is used to test a complex annotation type
@RuntimeVisibleTestAnnotations({
    @RuntimeVisibleTestAnnotation(name="a"),
    @RuntimeVisibleTestAnnotation(name="b")
})
public final class AnnotationDetectorTest {

    @RuntimeVisibleTestAnnotation(name = "test-constructor")
    public AnnotationDetectorTest() {

    }

    /** Used to test {@link AnnotationDetector#getField() }. */
    @RuntimeVisibleTestAnnotation(name = "test-field")
    private int testField;

    /** Used to test {@link AnnotationDetector#getMethod() }. */
    @RuntimeVisibleTestAnnotation(name = "test-method")
    public void idiot(
        boolean z, char c, byte b, short s, int i, long j, float f, double d, // primitives
        String str, int[] a, // basic object, enum and array
        int[][] ia, String[] stra
        ) throws IOException {
        // do nothing, just for parsing the method descriptor
    }

    @Test
    public void testOnType() throws IOException {
        List<Class<?>> types = AnnotationDetector.scanClassPath()
            .forAnnotations(RuntimeVisibleTestAnnotation.class)
            .collect(new ReporterFunction<Class<?>>() {

                @Override
                public Class<?> report(Cursor cursor) {
                    return cursor.getType();
                }

            });
        //System.out.println(types);
        assertEquals(1, types.size());
        assertSame(AnnotationDetectorTest.class, types.get(0));
    }

    @Test
    public void testOnConstructor() throws IOException {
        List<Constructor> constructors = AnnotationDetector.scanClassPath("eu.infomas")
            .forAnnotations(RuntimeVisibleTestAnnotation.class)
            .on(ElementType.CONSTRUCTOR)
            .filter(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(thisClassName());
                }
            })
            .collect(new ReporterFunction<Constructor>() {

                @Override
                public Constructor report(Cursor cursor) {
                    return cursor.getConstructor();
                }

            });

        //System.out.println(constructors);
        assertEquals(1, constructors.size());
        assertEquals("public eu.infomas.annotation.AnnotationDetectorTest()", constructors.get(0).toString());
    }

    @Test
    public void testOnMethod() throws IOException {
        List<Method> methods = AnnotationDetector.scanClassPath("eu.infomas")
            .forAnnotations(RuntimeVisibleTestAnnotation.class)
            .on(ElementType.METHOD)
            .filter(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(thisClassName());
                }
            })
            .collect(new ReporterFunction<Method>() {

                @Override
                public Method report(Cursor cursor) {
                    return cursor.getMethod();
                }

            });

        //System.out.println(methods);
        assertEquals(1, methods.size());
        assertEquals(
            "public void eu.infomas.annotation.AnnotationDetectorTest.idiot(" +
            "boolean,char,byte,short,int,long,float,double,java.lang.String,int[],int[][]," +
            "java.lang.String[]) throws java.io.IOException", methods.get(0).toString());
    }

    @Test
    public void testField() throws IOException {
        List<Field> fields = AnnotationDetector.scanClassPath("eu.infomas.annotation")
            .forAnnotations(RuntimeVisibleTestAnnotation.class)
            .on(ElementType.FIELD)
            .filter(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(thisClassName());
                }
            })
            .collect(new ReporterFunction<Field>() {

                @Override
                public Field report(Cursor cursor) {
                    return cursor.getField();
                }

            });

        System.out.println(fields);
        assertEquals(1, fields.size());
        assertEquals(
            "private int eu.infomas.annotation.AnnotationDetectorTest.testField",
            fields.get(0).toString());
    }

//    @Test
//    public void testLambdas() throws IOException {
//        List<Class<?>> types = AnnotationDetector.scanClassPath()
//            .forAnnotations(RuntimeVisibleTestAnnotation.class)
//            .collect(Cursor::getType);
//
//        assertEquals(1, types.size());
//        assertSame(AnnotationDetectorTest.class, types.get(0));
//
//        List<Method> methods = AnnotationDetector.scanClassPath("eu.infomas")
//            .forAnnotations(RuntimeVisibleTestAnnotation.class)
//            .on(ElementType.METHOD)
//            .filter((File dir, String name) -> name.endsWith("Test.class"))
//            .collect(Cursor::getMethod);
//
//        assertEquals(1, methods.size());
//        assertEquals("idiot", methods.get(0).getName());
//        assertEquals(void.class, methods.get(0).getReturnType());
//        assertEquals("test-method", methods.get(0)
//            .getAnnotation(RuntimeVisibleTestAnnotation.class).name());
//
//        final Class<RuntimeVisibleTestAnnotation> atype = RuntimeVisibleTestAnnotation.class;
//        List<String> names = AnnotationDetector.scanClassPath("eu.infomas")
//            .forAnnotations(atype)
//            .on(ElementType.METHOD)
//            .filter((File dir, String name) -> name.endsWith("Test.class"))
//            .collect(detector -> detector.getAnnotation(atype).name());
//
//        assertEquals(1, names.size());
//        assertEquals("test-method", names.get(0));
//    }

    private String thisClassName() {
        return getClass().getSimpleName() + ".class";
    }

}
