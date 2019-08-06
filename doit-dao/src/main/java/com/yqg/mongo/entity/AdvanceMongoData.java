package com.yqg.mongo.entity;

import com.yqg.base.data.condition.BaseMongoEntity;
import com.yqg.base.mongo.anaotations.Table;
import lombok.Getter;
import lombok.Setter;

@Table("advanceMongoData")
@Getter
@Setter
public class AdvanceMongoData extends BaseMongoEntity {
    private String userUuid;
    private String orderNo;
    private String requestType;
    private String responseData;
}
