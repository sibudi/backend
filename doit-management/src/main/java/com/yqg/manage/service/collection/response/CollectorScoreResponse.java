package com.yqg.manage.service.collection.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Getter
@Setter
@ApiModel
public class CollectorScoreResponse {

    @ApiModelProperty(value = "用户姓名")
    private String userName;

    @ApiModelProperty(value = "count")
    private Integer count;

    @ApiModelProperty(value = "评分时间")
    private Date createTime;

    @ApiModelProperty(value = "评分时间")
    private String createTimeStr;

    @ApiModelProperty(value = "userUuid")
    private String userUuid;

    @ApiModelProperty("总分")
    private float totalScore;

    @ApiModelProperty("所属阶段")
    private String postId;

    @ApiModelProperty("服务意识总分")
    private Integer serviceMentality;

    @ApiModelProperty("沟通能力总分")
    private Integer communicationBility;

    @ApiModelProperty("avg服务意识总分")
    private BigDecimal avgServiceMentality;

    @ApiModelProperty("avg沟通能力总分")
    private BigDecimal avgCommunicationBility;
}
