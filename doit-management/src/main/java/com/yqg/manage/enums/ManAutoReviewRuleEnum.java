package com.yqg.manage.enums;

public enum ManAutoReviewRuleEnum {
    //
    DATA_EMPTY(-1, "DATA_EMPTY"),
    //
    FIRST_CHECK_A(1,"FIRST_CHECK_A"),
    //
    FIRST_CHECK_B(2,"FIRST_CHECK_B"),
    //
    FIRST_CHECK_C(3,"FIRST_CHECK_C"),
    //
    SECOND_CHECK(4,"SECOND_CHECK");

    ManAutoReviewRuleEnum(int type, String message) {
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
