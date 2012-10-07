package eu.infomas.test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Copied from: <a href="http://stackoverflow.com/questions/3089151">Specifying an order
 * to junit 4 tests at the Method level (not class level)</a>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Order {

    public int value();
}
