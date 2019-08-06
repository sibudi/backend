package com.yqg.mongo.entity;

import com.yqg.base.data.condition.BaseMongoEntity;
import com.yqg.base.mongo.anaotations.Table;
import lombok.Data;

import java.util.Date;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
@Table("UserContactsMongo")
public class UserContactsMongo extends BaseMongoEntity {

    private String data;

    private String userUuid;

    private String orderNo;

}
