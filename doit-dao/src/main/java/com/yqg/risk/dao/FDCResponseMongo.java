package com.yqg.risk.dao;

import com.yqg.base.data.condition.BaseMongoEntity;
import com.yqg.base.mongo.anaotations.Table;

import lombok.Getter;
import lombok.Setter;

@Table("fdcResponseMongo")
@Getter
@Setter
public class FDCResponseMongo extends BaseMongoEntity {

    private String orderNo;
    private String userUuid;
    private String idCardNo;
    private Integer reason;
    //private String reffId;
    private String requestData;
    private String responseData;

}