package com.yqg.third.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Table("iziWhatsAppDetail")
@Getter
@Setter
public class IziWhatsAppDetailEntity extends BaseEntity {
    private String orderNo;
    private String userUuid;
    private Integer type;
    private String status;
    private String mobileNumber;
    private String whatsapp; //是否开通，yes，no，checking
    private String statusUpdate; //状态更新时间
    private String signature;//签名
    private String businessUser;// yes or no 是否是企业账户
    private String avatar;//yes or no 是否有头像
    private String message;
}
