package com.yqg.risk.entity;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2019/6/23.
 */
@Data
public class Cash2TransferRate {

    private String repayDay;
    private String repayOrder;
    private String againBorrow;
    private String againBorRate;
    private String cashBorrow;
    private String ownBorrow;
    private String tranOwnRate;

}
