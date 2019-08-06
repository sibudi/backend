/*
 * Copyright (c) 2017-2018 , Inc. All Rights Reserved.
 */
package com.yqg.common.utils;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Jacob
 *
 */
public class GenerateFuncUtils {

    public static <T> void generateEquals(Class<T> clazz) {
        StringBuffer stringBuffer = new StringBuffer();
        String clazzName = clazz.getSimpleName();
        stringBuffer.append("    @Override").append("\n")
                .append("    public boolean equals(Object o) {").append("\n")
                .append("        if (this == o) {return true;}").append("\n")
                .append("        if (o == null || getClass() != o.getClass()) { return false; }")
                .append("\n").append("\n").append("        ")
                .append(clazzName).append(" other = (").append(clazzName).append(") o;")
                .append("\n").append("\n")
                .append("        return ");

        for (Method method : clazz.getDeclaredMethods()) {
            String methodName = method.getName();
            if (methodName.startsWith("get") || methodName.startsWith("is")) {
                stringBuffer.append("Objects.equals(this.").append(methodName).append("(), other.")
                        .append(methodName).append("()) &&");
            }
        }
        stringBuffer.replace(stringBuffer.length() - 2, stringBuffer.length() - 0, ";")
                .append("\n    ").append("}").append("\n");
        
        System.out.println(stringBuffer.toString());
    }

    public static <T> void generateHashCode(Class<T> clazz) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("    @Override").append("\n")
                .append("    public int hashCode() {").append("\n")
                .append("        return Objects.hash(");
        for (Method method : clazz.getDeclaredMethods()) {
            String methodName = method.getName();
            if (methodName.startsWith("get") || methodName.startsWith("is")) {
                stringBuffer.append("this.").append(methodName).append("(), ");
            }
        }
        stringBuffer.replace(stringBuffer.length() - 2, stringBuffer.length() - 0, ");")
                .append("\n    ").append("}").append("\n");
        
        System.out.println(stringBuffer.toString());
    }

    public static <T> void generateToString(Class<T> clazz) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("    @Override").append("\n").append("    public String toString() {")
                .append("\n")
                .append("        return com.google.common.base.MoreObjects.toStringHelper(this)");
        for (Method method : clazz.getDeclaredMethods()) {
            String methodName = method.getName();
            if (methodName.startsWith("get")) {
                String filedName = StringUtils.removeStart(methodName, "get");
                stringBuffer
                        .append("\n")
                        .append("                .add(\"")
                        .append(StringUtils.lowerCase(filedName.substring(0, 1))
                                + StringUtils.substring(filedName, 1, filedName.length()))
                        .append("\", this.").append(methodName).append("())");
            } else if (methodName.startsWith("is")) {
                String filedName = StringUtils.removeStart(methodName, "is");
                stringBuffer
                        .append("\n")
                        .append("                .add(\"")
                        .append(StringUtils.lowerCase(filedName.substring(0, 1))
                                + StringUtils.substring(filedName, 1, filedName.length()))
                        .append("\", this.").append(methodName).append("())");
            }
        }
        stringBuffer.append("\n").append("                .toString()").append(";\n")
                .append("    }\n");
        
        System.out.println(stringBuffer.toString());
    }

    public static <T> void generateOverrideMethods(Class<T> clazz) {
        generateEquals(clazz);
        generateHashCode(clazz);
        generateToString(clazz);
    }

    public static void main(String args[]) {
        generateOverrideMethods(Object.class);
    }
}
