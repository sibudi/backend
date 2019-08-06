package com.yqg.mongo.entity;

import com.yqg.base.data.condition.BaseMongoEntity;
import com.yqg.base.mongo.anaotations.Table;
import lombok.Data;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/8
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Data
@Table("externalChannelData")
public class ExternalChannelDataMongo extends BaseMongoEntity {
    private String requestUri;
    private String decryptedText;
    private String channel;
    private String externalOrderNo;//传入的订单号
}
