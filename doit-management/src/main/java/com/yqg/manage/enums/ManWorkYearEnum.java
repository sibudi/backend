package com.yqg.manage.enums;

/**
 * ??????
 */
public enum ManWorkYearEnum {

    DEFALUT(0,"未填写"),
    NOT_KNOW(1,"未知"),
    THREE_MONTH_BELOW(2,"3个月以下"),
    THREE_SIX_MONTH(3,"3-6个月"),
    SIX_TWELE_MONTH(4,"6-12个月"),
    ONE_THREE_YEAR(5,"1-3年"),
    THREE_YEAR_UP(6,"3年以上");

    ManWorkYearEnum(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private int type;

    private String message;

}
