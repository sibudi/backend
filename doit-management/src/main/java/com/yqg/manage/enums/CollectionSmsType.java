package com.yqg.manage.enums;

/**
 * Created with IntelliJ IDEA.
 * User: caomiaoke
 * Date: 23/03/2018
 * Time: 1:59 PM
 */
public enum CollectionSmsType {

    CALL_RECORD_TOP_30(30,"通话频次前30"),
    CALL_RECORD_TOP_20(20,"通话频次前20"),
    CALL_RECORD_TOP_10(10,"通话频次前10"),
    CALL_RECORD_TOP_5(5,"通话频次前5");

    private int num;
    private String msg;

    CollectionSmsType(int num, String msg) {
        this.num = num;
        this.msg = msg;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

