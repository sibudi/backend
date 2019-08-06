/*
 * Copyright (c) 2017-2018 , Inc. All Rights Reserved.
 */
package com.yqg.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Jacob
 *
 */
public class GenerateMapperUtils {

    /**
     * gennerate simple sql mapping function
     * e.g.
     * @Select("select * from comment where id = #{id}")
     * public List<Comment> scan(@Param("id") Long id);
     *
     * @Insert("insert into comment(name) values (#{comment.name})")
     * public long add(@Param("comment") Comment comment);
     *
     * @Update("update comment set status = #{comment.status} where id = #{comment.id} ")
     * public void update(@Param("comment") Comment comment);
     *
     * @author Jacob
     * @param Class<T> e.g. Comment.class form entity package 
     * 
     */
    public static <T> void generateMapper(Class<T> clazz) {
        StringBuffer result = new StringBuffer();
        String clazzName = clazz.getSimpleName();
        String var = StringUtils.lowerCase(clazzName.substring(0, 1))
                + StringUtils.substring(clazzName, 1, clazzName.length());
        Field[] fields = clazz.getDeclaredFields();
        StringBuffer filedsStringBuffer = new StringBuffer();
        StringBuffer fieldsValueStringBuffer = new StringBuffer();
        for (Field field : fields) {
            if (!isStatic(field.getModifiers()) && isPrivate(field.getModifiers())) {
                filedsStringBuffer.append(field.getName()).append(", ");
                fieldsValueStringBuffer.append("#{").append(var).append(".").append(field.getName())
                        .append("},");
            }
        }
        String fieldsValueString = StringUtils.removeEnd(fieldsValueStringBuffer.toString(), ", ");
        String fieldsString = StringUtils.removeEnd(filedsStringBuffer.toString(), ", ");

        result.append("    @Select(\"select ").append(fieldsString).append(" from ")
                .append(var)
                .append(" where id = #{id}\")").append("\n")
                .append("    public List<").append(clazzName).append("> scan(@Param(\"id\") Long id);")
                .append("\n").append("\n");

        result.append("    @Insert(\"insert into ").append(var).append("(").append(fieldsString)
                .append(") values (").append(fieldsValueString).append(")\")").append("\n")
                .append("    public long add(@Param(\"").append(var).append("\") ")
                .append(clazzName).append(" ").append(var).append(");\n\n");

        result.append("    @Update(\"update ").append(var).append(" set status = #{")
                .append(var).append(".status} where id = #{").append(var).append(".id} \")")
                .append("\n").append("    public void update(@Param(\"").append(var)
                .append("\") ").append(clazzName).append(" ").append(var).append(");\n\n");

        System.out.println(result.toString());
    }

    private static boolean isStatic(int modifiers) {
        return ((modifiers & Modifier.STATIC) != 0);
    }

    private static boolean isPrivate(int modifiers) {
        return ((modifiers & Modifier.PRIVATE) != 0);
    }
}
