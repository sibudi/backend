package com.yqg.common.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by wanghuaizhou on 2018/12/26.
 */
@Inherited
@Retention(RUNTIME)
@Target({ TYPE })
public @interface CheetahRequest {
}
