package com.yqg.manage.service.order.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Author: tonggen
 * Date: 2019/2/26
 * time: 4:17 PM
 */
@Data
public class CouponRequest extends CouponBaseRequest implements Serializable{

    private String userUuid;
    private String orderNo;

    private String userName;

    private BigDecimal money;

    private String validityStartTime;

    private String validityEndTime;

    //发放人
    private Integer sendPersion;


    //发放时间
    private String sendStartTime;
    private String sendEndTime;
    //使用时间
    private String useStartTime;
    private String useEndTime;

    private String remark;

    private Integer createUser;

    @Override
    public String toString() {
        return "CouponRequest{" +
                "orderNo='" + orderNo + '\'' +
                ", userName='" + userName + '\'' +
                ", money=" + money +
                ", validityStartTime='" + validityStartTime + '\'' +
                ", validityEndTime='" + validityEndTime + '\'' +
                ", sendPersion=" + sendPersion +
                ", sendStartTime='" + sendStartTime + '\'' +
                ", sendEndTime='" + sendEndTime + '\'' +
                ", useStartTime='" + useStartTime + '\'' +
                ", useEndTime='" + useEndTime + '\'' +
                '}';
    }
}
