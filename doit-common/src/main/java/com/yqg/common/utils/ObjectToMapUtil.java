package com.yqg.common.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jacob
 */
public class ObjectToMapUtil {

    /**
     * object to map
     * @param obj
     * @return
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    public final static Map<String, Object> objectToMap(Object obj)
            throws IllegalArgumentException, IllegalAccessException {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            if (field.get(obj) != null) {
                map.put(field.getName(), String.valueOf(field.get(obj)));
            }
        }
        return map;
    }
}
