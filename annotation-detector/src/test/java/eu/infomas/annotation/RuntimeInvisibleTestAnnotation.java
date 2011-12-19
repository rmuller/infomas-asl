package eu.infomas.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// default retention policy creates a "RuntimeInvisibleAnnotation" attribute
@Retention(RetentionPolicy.CLASS)
public @interface RuntimeInvisibleTestAnnotation {
    
}
