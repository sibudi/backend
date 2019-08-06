package com.yqg.manage.service.collection.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Getter
@Setter
@ApiModel
public class CollectionAssignmentRequest {

    @ApiModelProperty(value = "本次分配的订单uuid列表")
    private List<String> orderUUIDs;

    @ApiModelProperty(value = "催收人员id")
    private Integer outsourceId;

    @ApiModelProperty(value = "true 表示委外 否则不是委外")
    private Boolean isThird = false;

    @ApiModelProperty(value = "来源 0 催收 1.质检")
    @JsonProperty
    private Integer sourceType = 0;

}
