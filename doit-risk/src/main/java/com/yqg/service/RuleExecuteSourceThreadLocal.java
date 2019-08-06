package com.yqg.service;

public class RuleExecuteSourceThreadLocal {
    private static ThreadLocal<String> executeSource = new ThreadLocal<>();

    public static void remove() {
        executeSource.remove();
    }

    public static String getSource() {
        return executeSource.get();
    }
    public static void setSource(ExecuteSourceEnum source){
        executeSource.set(source.name());
    }

    public enum ExecuteSourceEnum{
        TAX_RERUN,
        NORMAL;
    }
}
