package com.yqg.mongo.entity;

import com.yqg.base.data.condition.BaseMongoEntity;
import com.yqg.base.mongo.anaotations.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("OrderBlackTemp")
public class OrderBlackTempMongo extends BaseMongoEntity {
    private String orderNo;
    private String userUuid;
    private String ruleResult;
}
