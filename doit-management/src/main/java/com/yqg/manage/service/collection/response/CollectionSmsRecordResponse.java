package com.yqg.manage.service.collection.response;

import com.yqg.manage.enums.CollectionSmsType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: caomiaoke
 * Date: 26/03/2018
 * Time: 6:57 PM
 */
@Setter
@Getter
@ApiModel(description = "催收短信记录")
public class CollectionSmsRecordResponse {

    @ApiModelProperty("序号")
    private Integer id;

    @ApiModelProperty("发送人")
    private String sender;

    @ApiModelProperty("接收人类型")
    private CollectionSmsType receiveType;

    @ApiModelProperty("发送时间")
    private Date sendTime;

    @ApiModelProperty("短信标题")
    private String smsTitle;
}
