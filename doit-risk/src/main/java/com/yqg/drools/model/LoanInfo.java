package com.yqg.drools.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoanInfo {
    private Integer applyTimeHour;
    private Integer currentBorrowCount;
    private Integer historySubmitCount;// the total apply orders of this user (except for current order).

    private Long diffMinutesOfUserCreateTimeAndOrderSubmitTime; //the diff minutes between user create and order submit

    private Long diffMinutesOfStepOne2StepTwo;//the diff minutes between step one and step two

    private String borrowingPurpose;// the purpose of borrowing

    private BigDecimal borrowingAmount;// apply loan amount of this order

    private BigDecimal firstBorrowingAmount ;// the loan amount of the first order

    @Getter
    public enum LoanPurpose {
        EDUCATION(1, "Pendidikan"),//教育
        Decoration(2, "Renovasi"),//装修
        Travel(3, "Liburan"),//旅游
        Marriage(4, "Pernikahan"),//婚庆
        MobileAndDigital(5, "Gadget"),//手机数码
        Renting(6, "Uang sewa"),//租房
        Furniture(7, "Peralatan rumah tangga"),//家具家居
        HealthCare(8, "Pengobatan"),//健康医疗
        Other(9, "Lainnya"),//其它
        ;

        LoanPurpose(int code, String name) {
            this.code = code;
            this.name = name;
        }

        private int code;
        private String name;


    }

}
