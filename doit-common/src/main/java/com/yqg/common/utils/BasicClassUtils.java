/*
 * Copyright (c) 2014-2015 XXX, Inc. All Rights Reserved.
 */

package com.yqg.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * 
 * Utility functions for class.
 * 
 * @author Jacob
 *
 */
public class BasicClassUtils {

    public static final String NEW_LINE = System.getProperty("line.separator");

    public static final String HTML_NEW_LINE = "<br/>";

    public static final String BASE_HEADER = "-H 'appname:&lt;your_app_name&gt;' -H 'apptoken:&lt;your_app_token&gt;' -H 'Content-Type:application/json' "
            + HTML_NEW_LINE
            + "-H 'channel:mistore' -H 'timezone:&lt;client_timezone&gt;' -H 'lang: zh_CN' -H 'cv: 1.0.1' -H 'country: CN' -H 'callid:1233' "
            + HTML_NEW_LINE
            + "-H 'appplatform:ios_phone'  -H 'Accept: application/json' " + HTML_NEW_LINE;

    public static final String BASE_GET_CURL = "curl -X GET " + BASE_HEADER;

    public static final String BASE_POST_CURL = "curl -X POST "
            + BASE_HEADER;

    public static final String BASE_PUT_CURL = "curl -X PUT -H "
            + BASE_HEADER;

    public static final String BASE_DELETE_CURL = "curl -X DELETE "
            + BASE_HEADER;

    private static final Logger logger = LoggerFactory.getLogger(BasicClassUtils.class);

    /**
     * Get all public static fields of the class.
     * @param clazz
     * @return
     */
    public static List<Field> getPublicStaticFields(Class<?> clazz) {
        Field[] allFields = clazz.getDeclaredFields();
        List<Field> fields = new ArrayList<>(allFields.length);
        for (Field field : allFields) {
            int modifier = field.getModifiers();
            if (Modifier.isPublic(modifier) &&
                    Modifier.isStatic(modifier)) {
                fields.add(field);
            }
        }

        return Collections.unmodifiableList(fields);
    }

    /**
     * Get all private instance fields of the class.
     * @param clazz
     * @return
     */
    public static List<Field> getPrivateInstanceFields(Class<?> clazz) {
        Field[] allFields = clazz.getDeclaredFields();
        List<Field> fields = new ArrayList<>(allFields.length);
        for (Field field : allFields) {
            int modifier = field.getModifiers();
            if (Modifier.isPrivate(modifier) &&
                    !Modifier.isStatic(modifier)) {
                fields.add(field);
            }
        }

        return Collections.unmodifiableList(fields);
    }

    public static List<Field> getFields(Class<?> clazz, List<String> fieldNames) {
        List<Field> fields = new ArrayList<>();
        try {
            for (String fieldName : fieldNames) {
                Field f = clazz.getDeclaredField(
                        Preconditions.checkNotNull(fieldName));
                fields.add(Preconditions.checkNotNull(f));
            }
        } catch (NoSuchFieldException | SecurityException e) {
            logger.error("getFields error", e);
            throw new IllegalArgumentException(e);
        }

        return Collections.unmodifiableList(fields);
    }

    /**
     * Get field names from fields.
     * @param clazz
     * @return
     */
    public static List<String> toFieldNames(List<Field> fields) {
        List<String> results = fields.stream().map(f -> f.getName()).sorted()
                .collect(Collectors.toList());
        return Collections.unmodifiableList(results);
    }

    public static Object getInstanceField(Object instance, String fieldName)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException,
            IllegalAccessException {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(instance);
    }
}
