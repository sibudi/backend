package com.yqg.service.third.twilio.response;

import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.StringUtils;
import lombok.Data;

/**
 * Author: tonggen
 * Date: 2018/10/16
 * time: 上午11:17
 */
@Data
public class TwilioUserInfoResponse {

    private String orderNo;

    private String userUuid;

    private String phoneNumber;

    /**
     * 外呼需要的url
     */
    private String callUrl;

    private String callPhase; //varchar(10) not null default '' comment '外呼的订单阶段 D-1, D-2,D-3',
    private Integer callPhaseType; // tinyint not null default 0 comment '外呼的阶段类型 1.全部；2.未接通订单',
    private String batchNo; //批次号


    private Integer callNode;   // 外呼节点 1 机审到初审  2初审到复审
    private Integer callType;    // 外呼手机号类型（// 1本人电话 2公司电话 3 紧急联系人 4 备选联系人)

    private String from; //外显电话号码

    private String remark; //添加一个备注

    public TwilioUserInfoResponse(){}

    public TwilioUserInfoResponse(String orderNo, String userUuid, String phoneNumber, String callUrl,
    String callPhase, Integer callPhaseType, String batchNo) {
        this.orderNo = orderNo;
        this.userUuid = userUuid;
        this.callUrl = callUrl;
        this.callPhase = callPhase;
        this.callPhaseType = callPhaseType;
        this.batchNo = batchNo;
        if (StringUtils.isNotBlank(phoneNumber)) {
            String temp = CheakTeleUtils.telephoneNumberValid2(DESUtils.decrypt(phoneNumber));
            if (StringUtils.isNotBlank(temp)) {
                this.phoneNumber = "+62" + temp;
            }
        }
//        if ("+6287787117873".equals(phoneNumber)) {
////        if ("+6287787117873".equals(phoneNumber)) {
//            this.phoneNumber = phoneNumber;
//        } else {
//            if (StringUtils.isNotBlank(phoneNumber)) {
//                String temp = CheakTeleUtils.telephoneNumberValid2(DESUtils.decrypt(phoneNumber));
//                if (StringUtils.isNotBlank(temp)) {
//                    this.phoneNumber = "+62" + temp;
//                }
//            }
//        }
    }
}
