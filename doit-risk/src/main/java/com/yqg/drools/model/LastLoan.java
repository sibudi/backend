package com.yqg.drools.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


/***
 * 上一笔贷款信息
 */

@Getter
@Setter
public class LastLoan {

	private Long overdueDays = 0L;

	private BigDecimal borrowingAmount;

	private Boolean hitIncreaseLoanLimit200RMB;


	private Long intervalDays; // the diffDays for current loan and last loan

}
