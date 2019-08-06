package com.yqg.manage.service.user.request;

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
public class ManUserUserRequest{
    @ApiModelProperty(value = "手机号码")
    @JsonProperty
    private String mobile;
    @ApiModelProperty(value = "真实姓名")
    @JsonProperty
    private String realName;
    @ApiModelProperty(value = "注册开始时间")
    @JsonProperty
    private String startTime;
    @ApiModelProperty(value = "注册结束时间")
    @JsonProperty
    private String endTime;
    @ApiModelProperty(value = "注册渠道")
    @JsonProperty
    private Integer channel;
    @ApiModelProperty(value = "userUuid")
    @JsonProperty
    private String userUuid;
    @ApiModelProperty(value = "问题类型")
    @JsonProperty
    private Integer questionType;
    @ApiModelProperty(value = "解决情况")
    @JsonProperty
    private Integer stageType;
    @ApiModelProperty(value = "页数")
    @JsonProperty
    private Integer pageNo = 1;
    @ApiModelProperty(value = "分页大小")
    @JsonProperty
    private Integer pageSize = 10;
    @ApiModelProperty(value = "借款身份")
    @JsonProperty
    private Integer userRole;
    @ApiModelProperty(value = "0用户反馈；1.催收投诉")
    @JsonProperty
    private Integer sourceType;
    @ApiModelProperty(value = "被投诉催收人名")
    @JsonProperty
    private String collectionName;

}
