package com.yqg.manage.service.order.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author alan
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManOrderUserRequest {
    @ApiModelProperty(value = "订单编号")
    @JsonProperty
    private String orderNo;
    @ApiModelProperty(value = "用户uuid")
    @JsonProperty
    private String userUuid;
    @ApiModelProperty(value = "数据类型")
    @JsonProperty
    private Integer type;
    @ApiModelProperty(value = "语言 1中文 2印尼文")
    @JsonProperty
    private Integer langue;

    @ApiModelProperty(value = "判断是否是质检过来的，其查询图片不一样")
    @JsonProperty
    private Boolean checkQualityOrNot = false;
}
