package com.yqg.manage.service.order.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/*****
 * @Author Jacob
 *
 ****/

@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompanyTeleQARequest {

    @ApiModelProperty(value = "公司电核问题")
    @JsonProperty
    private String question;
    @ApiModelProperty(value = "公司电核答案")
    @JsonProperty
    private Integer answer;
    @ApiModelProperty(value = "问题的顺序备注")
    @JsonProperty
    private String remark;

}
