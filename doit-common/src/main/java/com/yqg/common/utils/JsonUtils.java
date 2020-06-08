/*
 * Copyright (c) 2014-2015 XXX, Inc. All Rights Reserved.
 */

package com.yqg.common.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Jacob
 *
 */
public class JsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
    }


    public static <T> T deserializeConfig(String string, Class<T> clazz) {
        if (StringUtils.isBlank(string)) {
            throw new IllegalArgumentException(
                    "Blank string cannot be deserialized to class");
        }
        try {
            objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER,true);
            T t = objectMapper.readValue(string, clazz);
            return t;
        } catch (IOException e) {
            logger.error(
                    "Error deserializing string: " + string, e);
            throw new IllegalArgumentException(
                    "Error deserializing string: " + string, e);
        }
    }

    public static <T> T deserialize(String string, Class<T> clazz) {
        if (StringUtils.isBlank(string)) {
            throw new IllegalArgumentException(
                    "Blank string cannot be deserialized to class");
        }

        try {
            T t = objectMapper.readValue(string, clazz);
            return t;
        } catch (IOException e) {
            logger.error(
                    String.format("Error deserializing string (last 300 characters): %.300s", e));
            throw new IllegalArgumentException(
                    "Error deserializing string (last 300 characters): %.300s", e);
        }
    }
    public static <T> T deserializeIgnoreError(String string, Class<T> clazz) {
        if (StringUtils.isBlank(string)) {
            throw new IllegalArgumentException(
                    "Blank string cannot be deserialized to class");
        }

        try {
            T t = objectMapper.readValue(string, clazz);
            return t;
        } catch (IOException e) {
            logger.error(
                    "Error deserializing string: " + string, e);
            return null;
        }
    }

    public static <T> T deserialize(String string, TypeReference<T> typeReference) {
        if (StringUtils.isBlank(string)) {
            throw new IllegalArgumentException(
                    "Blank string cannot be deserialized to class");
        }

        try {
            T t = objectMapper.readValue(string, typeReference);
            return t;
        } catch (IOException e) {
            logger.error(
                    "Error deserializing string: " + string, e);
            throw new IllegalArgumentException(
                    "Error deserializing string: " + string, e);
        }
    }

    public static String serialize(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Null Object cannot be serialized");
        }

        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing object", e);
            throw new IllegalArgumentException("Error serializing object: " + e.getMessage());
        }
    }

    public static boolean isJSONValid(String string) {
        try {
            objectMapper.readTree(string);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static <T> boolean isJSONValid(String string, Class<T> clazz) {
        if (StringUtils.isBlank(string)) {
            return false;
        }

        try {
            objectMapper.readValue(string, clazz);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static Map<String, Object> toMap(String jsonStr) {
        try {
            return (Map)objectMapper.readValue(jsonStr, Map.class);
        } catch (Exception var2) {
            logger.error("json parse error, jsonStr= " + jsonStr, var2);
            return null;
        }
    }

    public static <T> List<T> toList(String jsonStr, Class<T> clazz) {
        JavaType javaType = objectMapper.getTypeFactory()
                .constructParametricType(ArrayList.class, clazz);
        try {
            return objectMapper.readValue(jsonStr, javaType);
        } catch (IOException e) {
            logger.error("parse json error, jsonStr: " + jsonStr, e);
            return null;
        }
    }



}
