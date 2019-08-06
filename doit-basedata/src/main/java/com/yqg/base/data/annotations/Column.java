package com.yqg.base.data.annotations;

import static java.lang.annotation.ElementType.FIELD;
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
@Target({ FIELD })
public @interface Column {

    String value() default "";
}
