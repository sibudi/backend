package com.yqg.drools.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReBorrowScoreModel {
    private BigDecimal avgCollectionTimes;  //average collection times for this user

    private BigDecimal diffHoursForLastCollectionToRefund;

    private Long overdueDaysForLastLoan;

    private String sameOfWhatsAppAndMobileAndAcademic;

    private String firstLoanPassTypeAndMonthlyIncome;

    private String lastNegative2LoanOverdueDays; // the overdue days of n-2 loan (current loan is n)

    private String avgDaysOfApplyLoanGap;


    private String lastNegative3LoanOverdueDays;

    private String hasCreditCardAndAge;


    private String mobileLanguage;

    private String totalMemory;



}
