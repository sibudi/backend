/*
 * Copyright (c) 2014-2015 XXX, Inc. All Rights Reserved.
 */

package com.yqg.common.converter;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Source;

import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.util.ClassUtils;

/**
 * @author Jacob
 *
 */
public class CustomAllEncompassingFormHttpMessageConverter extends FormHttpMessageConverter {

    private static final boolean jaxb2Present = ClassUtils.isPresent("javax.xml.bind.Binder",
            CustomAllEncompassingFormHttpMessageConverter.class.getClassLoader());

    private static final boolean jackson2Present = ClassUtils.isPresent(
            "com.fasterxml.jackson.databind.ObjectMapper",
            CustomAllEncompassingFormHttpMessageConverter.class.getClassLoader()) &&
            ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator",
                    CustomAllEncompassingFormHttpMessageConverter.class.getClassLoader());

    public CustomAllEncompassingFormHttpMessageConverter() {
        List<HttpMessageConverter<?>> partConverters = new ArrayList<>();
        partConverters.add(new ByteArrayHttpMessageConverter());
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(
                Charset.forName("UTF-8"));
        stringHttpMessageConverter.setWriteAcceptCharset(false);
        partConverters.add(stringHttpMessageConverter);
        partConverters.add(new ResourceHttpMessageConverter());
        partConverters.add(new SourceHttpMessageConverter<Source>());
        if (jaxb2Present) {
            partConverters.add(new Jaxb2RootElementHttpMessageConverter());
        }
        if (jackson2Present) {
            partConverters.add(new MappingJackson2HttpMessageConverter());
        }
        setPartConverters(partConverters);
    }
}
