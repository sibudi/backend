package com.yqg.mongo.entity;

import com.yqg.base.data.condition.BaseMongoEntity;
import com.yqg.base.mongo.anaotations.Table;
import lombok.Data;

/**
 * Created by wanghuaizhou on 2019/2/28.
 */
@Data
@Table("MoboxDataMongo")
public class MoboxDataMongo extends BaseMongoEntity {

    private String orderNo;

    private String userUuid;

    private Integer type;

    private String taskId;

    private String data;

    private String resultState;

}