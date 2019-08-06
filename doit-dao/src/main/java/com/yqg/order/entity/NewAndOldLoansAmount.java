package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by Didit Dwianto on 2018/2/2.
 */
@Data
public class NewAndOldLoansAmount {
    private String lendDay;
    private String newLendDay;
    private String newLendAmount;
    private String oldLendNum;
    private String oldLendAmount;
    private String allLendNum;
    private String allLendAmount;
}
