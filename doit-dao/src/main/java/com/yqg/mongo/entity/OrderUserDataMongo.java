package com.yqg.mongo.entity;

import com.yqg.base.data.condition.BaseMongoEntity;
import com.yqg.base.mongo.anaotations.Table;
import lombok.Data;

import java.util.Date;

/**
 * Created by Didit Dwianto on 2017/11/26.
 */
@Data
@Table("OrderUserData")
public class OrderUserDataMongo extends BaseMongoEntity {


    private String orderNo;

    private String userUuid;

    private Integer infoType;

    private Object data;

    private Integer status;
}