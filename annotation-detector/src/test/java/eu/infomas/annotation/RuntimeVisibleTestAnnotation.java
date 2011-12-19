package eu.infomas.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// see RuntimeVisibleTestAnnotations
@Retention(RetentionPolicy.RUNTIME)
public @interface RuntimeVisibleTestAnnotation {
 
    String name() default "";
    
}
