package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2019/1/21.
 */
@Data
public class DayLoanCount2 {

    private String lendDate;
    private String totalLendNum;
    private String oldLendNum;
    private String newLendNum;
    private String newProportion;
    private String oldProportion;

}
