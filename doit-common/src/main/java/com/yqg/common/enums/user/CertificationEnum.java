package com.yqg.common.enums.user;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
public enum CertificationEnum {

    USER_IDENTITY(1), //Verified
    FACE_IDENTITY(2), //Face recognition
    VIDEO_IDENTITY(3), //Video certification
    FACEBOOK_IDENTITY(4), // faceBook
    XL_IDENTITY(5), // XL(Operator)
    TELK_IDENTITY(6), // TELK(Operator)
    TELK2_IDENTITY(7), // TELK(Operator)
    IM3_IDENTITY(8), // IM3(Operator)
    TOKOPEDIA_IDENTITY(9), // TokoPedia(E-commerce)
    GOJECK_IDENTITY(10), // Gojeck(E-commerce)
    GOLIFE_IDENTITY(11), // Golife(E-commerce)
    STEUERKARTED(12), // tax card
    INSURANCE_CARD(13), // Insurance Card
    WHATS_APP(14), // whatsapp account
    NPWP(15), // NPWP
    BPJS(16), // BPJS
    OPERATOR(17), // Operator
    LINKEDIN(18) // Linkedin
    ;

    private int type;

    private CertificationEnum(int type) {
        this.setType(type);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
