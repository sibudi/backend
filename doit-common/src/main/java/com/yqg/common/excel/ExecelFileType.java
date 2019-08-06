package com.yqg.common.excel;

/**
 * ????
 *
 * @author Jacob
 * @date 2016?6?10? @ FileNameCreateUtil.java
 */
public enum ExecelFileType {
	/**
	 * ???
	 */
	test_template("test")
	,
	/**
	 * ?????????
	 */
	allOverDue_template("allOverDue")
	,newOverDue_template("newOverDue")
	,oldOverDue_template("oldOverDue")
	,omniAmountStatistics_template("omniAmountStatistics")
	,omniAmountDetail_template("omniAmountDetail")
	,omniAmountFailDetail_template("omniAmountFailDetail")
	,R360OverDue_template("R360OverDue")
	,R360NewOverDue_template("R360NewOverDue")
	,R360OldOverDue_template("R360OldOverDue")
	,LateFees_template("LateFees")
	,XjbkOverDue_template("XjbkOverDue")
	,XjbkNewOverDue_template("XjbkNewOverDue")
	,XjbkOldOverDue_template("XjbkOldOverDue")
	,NatureOverDue_template("NatureOverDue")
	,NatureNewOverDue_template("NatureNewOverDue")
	,totalAccount_template("totalAccount")
	,totalAccountxlsx_template("totalAccountxlsx")
	,applyConversion_template("applyConversion")
	,badDebtEveryDay_template("badDebtEveryDay")
	,NatureOldOverDue_template("NatureOldOverDue")
	,FinancialForweekly_template("FinancialForweekly")
	,dayOverdue_template("dayOverdue")
	,dayOverdueNew_template("dayOverdueNew")
	,dayOverdueOld_template("dayOverdueOld")
	,dayCallNumber_template("dayCallNumber")
	;

	private String value;

	private ExecelFileType(String value) {
		this.setValue(value);
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
}