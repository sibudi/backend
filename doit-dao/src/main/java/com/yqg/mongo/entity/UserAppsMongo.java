package com.yqg.mongo.entity;

import com.yqg.base.data.condition.BaseMongoEntity;
import com.yqg.base.mongo.anaotations.Table;
import lombok.Data;

import java.util.Date;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
@Table("UserAppsMongo")
public class UserAppsMongo extends BaseMongoEntity {

    // 用户的app应用 json
    private String data;

    // 用户uuid
    private String userUuid;

    // 客户端类型
    private String clientType;

    // 订单号
    private String orderNo;
}
