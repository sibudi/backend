package com.yqg.base.data.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Jacob on 2017/7/31.
 */
@Inherited
@Retention(RUNTIME)
@Target({ FIELD })
public @interface Condition {

    String value() default "";
}
