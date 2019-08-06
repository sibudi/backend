package com.yqg.common.enums.user;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
public enum CertificationEnum {

    USER_IDENTITY(1), //实名认证
    FACE_IDENTITY(2), //人脸识别
    VIDEO_IDENTITY(3), //视频认证
    FACEBOOK_IDENTITY(4), // faceBook
    XL_IDENTITY(5), // XL(运营商)
    TELK_IDENTITY(6), // TELK(运营商)
    TELK2_IDENTITY(7), // TELK(运营商)
    IM3_IDENTITY(8), // IM3(运营商)
    TOKOPEDIA_IDENTITY(9), // TokoPedia(电商)
    GOJECK_IDENTITY(10), // Gojeck(电商)
    GOLIFE_IDENTITY(11), // Golife(电商)
    STEUERKARTED(12), // 税卡
    INSURANCE_CARD(13), // 保险卡
    WHATS_APP(14), // whatapp账号
    NPWP(15), // NPWP
    BPJS(16), // BPJS
    OPERATOR(17), // 运营商
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
