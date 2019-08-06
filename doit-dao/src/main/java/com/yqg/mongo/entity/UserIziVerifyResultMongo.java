package com.yqg.mongo.entity;

import com.yqg.base.data.condition.BaseMongoEntity;
import com.yqg.base.mongo.anaotations.Table;
import lombok.Data;

/**
 * Created by wanghuaizhou on 2019/3/18.
 */
@Data
@Table("UserIziVerifyResultMongo")
public class UserIziVerifyResultMongo extends BaseMongoEntity {
    private String userUuid;
    private String orderNo;
    private String iziVerifyType;  // Izi验证类型（1在网时长 2 手机号码实名认证）
    private String iziVerifyResult; // Izi验证结果
    private String iziVerifyResponse; // Izi验证返回response

    private String whatsapp;//yes or no or checking .
    private String whatsAppNumber;
    private String whatsAppNumberType;//0本人

    private String requestParam;
}
