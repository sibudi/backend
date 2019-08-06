package com.yqg.common.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
* @author Jacob
*
*/
@Inherited
@Retention(RUNTIME)
@Target({ TYPE })
public @interface H5Request {
    String value() default "";

}
