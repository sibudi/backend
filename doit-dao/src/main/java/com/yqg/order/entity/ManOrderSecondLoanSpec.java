package com.yqg.order.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;

/**
 * @author alan
 */
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManOrderSecondLoanSpec {
    private String userUuid;    /*复借用户uuid*/

    private Integer userCount;  /*复借用户订单数*/

    public String getUserUuid() {
        return userUuid;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }
}
