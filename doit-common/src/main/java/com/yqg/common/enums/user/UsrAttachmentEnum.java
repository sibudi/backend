package com.yqg.common.enums.user;

public enum UsrAttachmentEnum {
    ID_CARD(0),//Front photo of ID card
    HAND_ID_CARD(1),//Hand held ID card
    FACE(2),//Face photo
    VIDEO(3), //Video
    CREDIT_CARD(4), //Credit card
    SIM(5), // Driver's license
    NPWP(6), // Tax Card -----> July 6th has been replaced with a passport
    KK(7), //Family Card
    PAYROLL(8), //Payroll
    BANK_CARD_RECORD(9),//Bank card flow
    STUDENT_CARD(10),//Student ID
    SCHOLARSHIP(11),//Scholarship certificate
    ENGLISH(12),//English certificate
    COMPUTER(13),//Computer certificate
    SCHOOL_CARD(14),//Campus card
    OTHER_CERTIFICATION(15), //Other competition certificate
    WORK_PROOF(16),//Proof of work
    INSURANCE_CARD(17), //insurance card
    SELFIE(18); //Selfie

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