package com.yqg.manage.service.review.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class AutoReviewRuleResponse {

    @ApiModelProperty(value = "自动审核类型1:摇钱罐黑名单2:基础身份信息3:手机通讯录4:通话记录5:短信记录6:贷款App7:设备规则8:运营商9:手机系统")
    private Integer ruleType;

    @ApiModelProperty(value = "规则名称")
    private String ruleDesc;

    @ApiModelProperty(value = "规则条件")
    private String ruleCondition;

    @ApiModelProperty(value = "规则值")
    private String ruleValue;

    @ApiModelProperty(value = "规则状态")
    private Integer ruleStatus;

    @ApiModelProperty("规则uuid")
    private String uuid;

    @ApiModelProperty("规则id")
    private Integer id;

    @ApiModelProperty(value = "规则拒绝天数")
    private Integer ruleRejectDay;

    @ApiModelProperty("规则数据")
    private String ruleData;

}
