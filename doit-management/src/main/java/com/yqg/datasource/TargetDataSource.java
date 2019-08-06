package com.yqg.datasource;

import java.lang.annotation.*;

/**
 * @author alan
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {
    String name();
}
