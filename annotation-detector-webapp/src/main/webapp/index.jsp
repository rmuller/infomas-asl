<%@page import="java.io.File"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="eu.infomas.annotation.AnnotationDetector.TypeReporter"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.lang.annotation.Annotation"%>
<%@page import="eu.infomas.annotation.AnnotationDetector"%>
<%@page import="eu.infomas.annotation.AnnotationDetector.MethodReporter"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
 <head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>Test of INFOMAS AnnotationDetector 3.0.x in Webapp</title>
 </head>
 <body>
  <h1>Test of INFOMAS AnnotationDetector 3.0.x in Webapp</h1>
  <p>Detected Interfaces in Java 8 and package <code>eu.infomas.samples</code>, annotated with 
  <code>@FunctionalInterface</code>:</p>
  <table border="1">
   <%
       final long time = System.currentTimeMillis();
       final List<String> types = new ArrayList<>();
       final TypeReporter reporter = new TypeReporter() {

           @SuppressWarnings(  "unchecked")
           @Override
           public Class<? extends Annotation>[] annotations() {
               return new Class[]{FunctionalInterface.class};
           }

           @Override
           public void reportTypeAnnotation(Class<? extends Annotation> annotation, String className) {
               types.add(className);
           }

       };
       final AnnotationDetector cf = new AnnotationDetector(reporter);
       cf.detect(new File(new File(System.getProperty("java.home")), "lib/rt.jar"));
       cf.detect("eu.infomas.samples");
       for (String type : types) {
   %>
   <tr><td><%= type%></td></tr>
   <%
       }
   %>
  </table>
   <p>Scan done in <%= System.currentTimeMillis() - time %> ms. </p>
  <p>Class path</p>
  <table border="1">
   <%
       for (String elem : System.getProperty("java.class.path").split(File.pathSeparator)) {
   %>
   <tr><td><%= elem%></td></tr>
   <%
       }
   %>
  </table>
   <p>Java Version</p>
   <%= System.getProperty("java.version") %>
 </body>
</html>
