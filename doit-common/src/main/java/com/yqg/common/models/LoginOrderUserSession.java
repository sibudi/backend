package com.yqg.common.models;

/**
 * Created by Jacob on 2017/8/24.
 */
public class LoginOrderUserSession {
    private String userUuid;//userUUid
    private String orderNo;//订单号

    public LoginOrderUserSession() {
    }

    public LoginOrderUserSession(String userUuid, String orderNo) {
        this.userUuid = userUuid;
        this.orderNo = orderNo;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
