package com.yqg.manage.enums;

public enum ManOrderRemarkTypeEnum {
    //电话审核
    TELE_REVIEW(1,"个人电话审核"),
    //催收打标签
    COLLECTION_ORDERTAG(2,"催收打标签"),
    //公司电话审核
    COMPANY_TELE_REVIEW(3,"公司电话审核");

    ManOrderRemarkTypeEnum(int type, String message) {
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
