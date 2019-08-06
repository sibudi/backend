/*
 * Copyright (c) 2014-2015 XXX, Inc. All Rights Reserved.
 */

package com.yqg.common.utils;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

/**
 * @author Jacob
 *
 */
public class SystemUtils {

    private static final Logger logger = LoggerFactory.getLogger(SystemUtils.class);

//    private static final String HEADER_USER_AGENT = "User-Agent";

    public static void setHeaderSingleValue(MultiValueMap<String, String> headers,
            String headerName, String headerValue) {
        List<String> values = headers.get(headerName);
        if (!CollectionUtils.isEmpty(values)) {
            logger.warn("Header {} already has value {}, setting to {}", headerName, values,
                    headerValue);
            headers.remove(headerName);
        }
        headers.add(headerName, headerValue);
    }

}