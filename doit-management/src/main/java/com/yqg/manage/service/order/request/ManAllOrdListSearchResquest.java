package com.yqg.manage.service.order.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.enums.order.OrdStateEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Jacob
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManAllOrdListSearchResquest extends OrderSearchRequest{
    @ApiModelProperty(value = "是否复借订单")
    @JsonProperty
    private Integer isRepeatBorrowing;
    @JsonProperty
    private String createEndTime;
    @ApiModelProperty(value = "更新日期开始时间")
    @JsonProperty
    private String updateBeginTime;
    @ApiModelProperty(value = "更新日期结束时间")
    @JsonProperty
    private String updateEndTime;

//    @ApiModelProperty(value = "??uuid??")
//    @JsonProperty
//    private String uuidString ;

}
