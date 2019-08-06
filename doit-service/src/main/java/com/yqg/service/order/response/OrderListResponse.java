package com.yqg.service.order.response;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ??list??
 * Created by Didit Dwianto on 2017/11/29.
 */
@Data
public class OrderListResponse implements Serializable {
    private static final long serialVersionUID = -3144344753753746616L;
    private String orderNo;// ???
    private String orderStep; // ????
    private String orderStatus;// ????
    private String orderStatusMsg;// ??????
    private String amountApply;//???? 2
    private String borrowingTerm;//???? 3
    private String applyTime;//??????

}
