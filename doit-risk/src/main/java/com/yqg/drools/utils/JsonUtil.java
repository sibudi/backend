package com.yqg.drools.utils;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 *
 ****/

@Slf4j
public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
    }

    public static <T> T toObject(String jsonStr, Class<T> clazz) {
        if (StringUtils.isBlank(jsonStr)) {
            throw new IllegalArgumentException("Blank string cannot be deserialized to class");
        } else {
            try {
                T t = objectMapper.readValue(jsonStr, clazz);
                return t;
            } catch (IOException var3) {
                log.error("Error parse string: " + jsonStr, var3);
                throw new IllegalArgumentException("Error parse string: " + jsonStr, var3);
            }
        }
    }

    public static <T> List<T> toList(String jsonStr, Class<T> clazz) {
        JavaType javaType = objectMapper.getTypeFactory()
            .constructParametricType(ArrayList.class, clazz);
        try {
            return objectMapper.readValue(jsonStr, javaType);
        } catch (IOException e) {
            //log.error("parse json error, jsonStr: " + jsonStr, e);
            return null;
        }
    }
}
