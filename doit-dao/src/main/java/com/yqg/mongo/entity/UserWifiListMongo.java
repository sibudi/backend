package com.yqg.mongo.entity;

import com.yqg.base.data.condition.BaseMongoEntity;
import com.yqg.base.mongo.anaotations.Table;
import lombok.Data;

import java.util.Date;

/**
 * Created by Didit Dwianto on 2018/1/11.
 */
@Data
@Table("UserWifiListMongo")
public class UserWifiListMongo extends BaseMongoEntity {

    private String data;

    private String userUuid;

    private String orderNo;
}
