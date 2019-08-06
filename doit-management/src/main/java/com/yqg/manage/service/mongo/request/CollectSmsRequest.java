package com.yqg.manage.service.mongo.request;

import com.yqg.manage.enums.CollectionSmsType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created with IntelliJ IDEA.
 * User: caomiaoke
 * Date: 23/03/2018
 * Time: 6:12 PM
 */

@Setter
@Getter
@ApiModel
public class CollectSmsRequest {

    @ApiModelProperty(value = "订单编号")
    private String orderNo;

    @ApiModelProperty(value = "用户uuid")
    private String UserUuid;

    @ApiModelProperty(value = "审核人员uuid")
    private String manUuid;

    @ApiModelProperty(value = "短信模版ID")
    private String smsTemplateId;

    @ApiModelProperty(value = "收信人类别")
    private CollectionSmsType collectionSmsType;

    @ApiModelProperty(value = "催收订单的时间段")
    private String collectionLevel;
}
