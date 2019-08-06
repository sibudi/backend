package com.yqg.service.loan.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by wanghuaizhou on 2019/7/3.
 */
@Data
public class RepayPlanRequest {

//    债权编号
    private String creditorNo;

    private List<RepayPlan>  list;
    //签名
    private String sign;
}
