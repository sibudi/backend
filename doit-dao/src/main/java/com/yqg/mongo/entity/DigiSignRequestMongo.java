package com.yqg.mongo.entity;

import com.yqg.base.data.condition.BaseEntity;
import com.yqg.base.data.condition.BaseMongoEntity;
import com.yqg.base.mongo.anaotations.Table;
import lombok.Getter;
import lombok.Setter;

@Table("digSignRequestMongo")
@Getter
@Setter
public class DigiSignRequestMongo extends BaseMongoEntity {
    private String orderNo;
    private String userUuid;
    private String requestType;
    private String reqeustData;
    private String responseData;

    private String responseValid;//是否有效返回：true, false;


    public enum RequestTypeEnum{
        Registration,
        ACTIVATION,
        SEND_DOCUMENT,
        SIGN,
    }
}
