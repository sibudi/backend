package com.yqg.manage.enums;

public enum ManOrderCheckRuleEnum {
    BASEINFO_COMMON(1,"基本信息(基础)"),
    BASEINFO_STUDENT(2,"基本信息(学生)"),
    WORKINFO(3,"工作信息"),
    SCHOOLINFO(4,"学校信息"),
    LINKMANINFO(5,"联系人信息"),
    FACEINFO(6,"人脸识别"),
    VIDEOINFO(7,"视频认证"),
    ADDINFO_COMMON(8,"补充信息(基础)"),
    ADDINFO_STUDENT(9,"补充信息(学生)");

    private int code;
    private String message;

    private ManOrderCheckRuleEnum(int code,String message) {
        this.code=code;
        this.message=message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
