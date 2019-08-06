/*
 * Copyright (c) 2014-2015 XXX, Inc. All Rights Reserved.
 */

package com.yqg.config;

import com.yqg.common.configurations.DefaultSwaggerConfig;
import org.springframework.context.annotation.Configuration;



/**
 * @author Jacob
 *
 */
@Configuration
public class SwaggerConfig extends DefaultSwaggerConfig {

    public String getServiceName() {
        return "management";
    }

    

}
