package com.yqg.common.enums.user;

public enum UsrAttachmentEnum {
    ID_CARD(0),//身份证正面照
    HAND_ID_CARD(1),//手持身份证照
    FACE(2),//人脸照
    VIDEO(3),//视频
    CREDIT_CARD(4),//信用卡
    SIM(5),//驾驶证
    NPWP(6),//税卡 ----->  7月6号已替换成护照
    KK(7),//家庭卡
    PAYROLL(8),//工资单
    BANK_CARD_RECORD(9),//银行卡流水
    STUDENT_CARD(10),//学生证
    SCHOLARSHIP(11),//奖学金证书
    ENGLISH(12),//英语证书
    COMPUTER(13),//计算机证书
    SCHOOL_CARD(14),//校园卡
    OTHER_CERTIFICATION(15), //其他大赛证书
    WORK_PROOF(16),//工作证明
    INSURANCE_CARD(17), //保险卡
    SELFIE(18); //自拍照

    private int type;

    UsrAttachmentEnum(int  type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static UsrAttachmentEnum enumFromType(int type){
        for(UsrAttachmentEnum e: enums){
           if( e.getType() == type){
               return e;
           }
        }
        return null;
    }
    private static UsrAttachmentEnum enums[] =UsrAttachmentEnum.values();
}