package com.yqg.common.utils;

import java.lang.reflect.Field;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

/**
* @author Jacob
*
*/
public class GenerateBuilderUtils {

    private static final Random RANDOM = new Random(100000000);

    public static <T> void generateBuilder(Class<T> clazz) {
        StringBuilder fieldsString = new StringBuilder();
        StringBuilder buildFuncString = new StringBuilder();
        StringBuilder createTestModelString = new StringBuilder();

        String modelName = clazz.getSimpleName();
        String clazzName = StringUtils.remove(modelName, "Model") + "Builder";
        buildFuncString.append("    public ").append(modelName).append(" build() {\n")
                .append("        ").append(modelName).append(" result = new ").append(modelName)
                .append("();\n");

        createTestModelString.append("    public static ").append(modelName)
                .append(" createTestModel() {\n").append("        return new ").append(clazzName)
                .append("()\n");

        for (Field field : clazz.getDeclaredFields()) {
            fieldsString.append("    public ").append(clazzName).append(" ")
                    .append(field.getName()).append("(")
                    .append(field.getType().getSimpleName()).append(" ").append(field.getName())
                    .append(") {\n").append("        this.").append(field.getName()).append(" = ")
                    .append(field.getName()).append(";\n").append("        return this;")
                    .append("\n")
                    .append("    }").append("\n\n");

            buildFuncString
                    .append("        result.set")
                    .append(StringUtils.upperCase(field.getName().charAt(0) + "")
                            + StringUtils.substring(field.getName(), 1)).append("(this.")
                    .append(field.getName()).append(");").append("\n");
//            System.out.print(field.getGenericType().toString());
            if ("class java.lang.Long".equals(field.getGenericType().getTypeName())) {
                createTestModelString.append("                .").append(field.getName())
                .append("(").append(RANDOM.nextLong())
                .append(")\n");
            } else {
                createTestModelString.append("                .").append(field.getName())
                .append("(\"").append("nbf")
                .append("\")\n");
            }
        }

        buildFuncString.append("        return result;\n").append("    }\n\n");
        createTestModelString.append("                .build();\n").append("    }\n\n");

        System.out.println(fieldsString.toString() + buildFuncString.toString()
                + createTestModelString.toString());
    }

    public static void main(String args[]) {
        //        List<Class<?>> clazzes = Arrays.asList(ContentModel.class);
        //        for (Class<?> clazz : clazzes) {
        //            generateBuilder(clazz);
        //        }
//        generateBuilder(UserModel.class);
//        System.out.println(JsonUtils.serialize(UserBuilder.createTestModel()));
    }
}
