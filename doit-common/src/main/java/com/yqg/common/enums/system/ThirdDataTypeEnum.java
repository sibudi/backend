package com.yqg.common.enums.system;

/**
 * Created by Didit Dwianto on 2017/11/26.
 */
public enum ThirdDataTypeEnum {

    YITU_DATA(1), //??
     IDENTITY_CHECK_DATA(2), //??????
    FACEBOOK_IDENTITY_DATA(4), // faceBook
    XL_IDENTITY_DATA(5), // XL(???)
    TELK_IDENTITY_DATA(6), // TELK(???)
    TELK2_IDENTITY_DATA(7), // TELK2(???)
    IM3_IDENTITY_DATA(8), // IM3(???)
    TOKOPEDIA_IDENTITY_DATA(9), // TokoPedia(??)
    GOJECK_IDENTITY_DATA(10),// Gojeck(??)
    GOLIFE_IDENTITY_DATA(11),// Golife(??)
    TAX_NUMBER_VERIFY_DATA(12),//税卡实名数据
    XENDIT_IDENTITY_CHECK(13),//xendit实名验证数据
    ASLI_IDENTITY_CHECK(14), //Asli实名验证数据
    ASLI_SELFIE_CHECK(15),//Asli自拍照数据
    ASLI_PLUS_VERIFICATION(16); //Asli plus verification


    private int type;

    private ThirdDataTypeEnum(int type) {
        this.setType(type);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
