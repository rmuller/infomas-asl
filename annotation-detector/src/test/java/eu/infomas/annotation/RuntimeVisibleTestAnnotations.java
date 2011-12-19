package eu.infomas.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// see RuntimeVisibleTestAnnotation
@Retention(RetentionPolicy.RUNTIME)
public @interface RuntimeVisibleTestAnnotations {
    
    RuntimeVisibleTestAnnotation[] value();
    
}
