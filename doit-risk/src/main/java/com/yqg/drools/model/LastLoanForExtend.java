package com.yqg.drools.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LastLoanForExtend extends LastLoan {

    //the model score of last loan
    private ModelScoreResult lastLoanModelScore;

    private Long diffHoursBetweenFirstCollectionAndRefundTime;

    private Integer loanPassType; //1:免核 2:公司 3:人工

    private Boolean noCollectionRecord;

}
