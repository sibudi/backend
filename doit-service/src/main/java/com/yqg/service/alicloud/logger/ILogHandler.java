package com.yqg.service.alicloud.logger;

public interface ILogHandler {

    public enum LogType {
        LOCAL, FUNCTION_COMPUTE, LOG_STORE
    }

    public enum LogCategory {
        INFO("info"), WARN("warn"), ERROR("error"), DEBUG("debug");

        private final String value;
        public String getValue() {
            return this.value;
        }
        private LogCategory(String value) {
            this.value = value;
        }
    }

    public void debug(String format, Object... arguments);
    public void info(String format, Object... arguments);
    public void warn(String format, Object... arguments);
    public void error(String format, Object... arguments);
    
    public void error(String message, Throwable t);
}