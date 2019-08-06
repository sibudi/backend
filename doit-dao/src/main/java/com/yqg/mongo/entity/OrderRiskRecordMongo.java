package com.yqg.mongo.entity;

import com.yqg.base.data.condition.BaseMongoEntity;
import com.yqg.base.mongo.anaotations.Table;
import com.yqg.order.entity.OrdRiskRecord;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Table("OrderRiskRecord")
public class OrderRiskRecordMongo extends BaseMongoEntity {
    private String orderNo;
    private String userUuid;
    private List<OrdRiskRecord> ruleResult;
}
