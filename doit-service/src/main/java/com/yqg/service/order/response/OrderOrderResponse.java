package com.yqg.service.order.response;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * ????
 * Created by Didit Dwianto on 2017/11/26.
 */
@Data
public class OrderOrderResponse implements Serializable {
    private static final long serialVersionUID = 8429534648697238151L;
    private String orderNo;// 订单号
    private String orderStep; //订单步骤
    private String showState;//展示标识
    private String userRole;//用户标识
    private String orderStatus;//订单状态
    private String orderStatusMsg;//订单状态名称

    private String isAgain;  //是否是复借

    private ForwardStepEnum forwardStep; //跳转步骤
    public enum ForwardStepEnum{
        DEFAULT,
        LINKMAN
    }


    private String rate1;
    private String rate2;
    private String rate3;
    private String rate4;
    private String rate5;
    private String rate6;

    private String repayRate;

    private String orderType;

    private String userScore; //用户评分

    private String isNeedQA; //是否需要填写问卷 0 未填写过 需要 1 填写过 不需要
}
