package com.yqg.manage.service.review.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
public class AutoReviewRuleParam {

    @ApiModelProperty(value = "自动审核类型1:摇钱罐黑名单2:基础身份信息3:手机通讯录4:通话记录5:短信记录6:贷款App7:设备规则8:运营商9:手机系统")
    private Integer ruleType;//类型

    @ApiModelProperty("规则名称")
    private String ruleDesc;//规则名称

    @ApiModelProperty(value = "规则条件")
    private String ruleCondition;//规则条件

    @ApiModelProperty(value = "规则值")
    private String ruleValue;//规则值

    @ApiModelProperty(value = "规则状态(1有效 2无效 3测试)")
    private Integer ruleStatus;

    @ApiModelProperty(value = "当前规则uuid")
    private String uuid;

    @ApiModelProperty(value = "规则拒绝天数")
    private Integer ruleRejectDay;

    @ApiModelProperty("规则数据")
    private String ruleData;

    @Getter
    public enum RuleTypeEnum {
        YQG_BLACKLIST(1, "摇钱罐黑名单"),
        BASE_IDENTITY_RULE(2, "基础身份信息"),
        MOBILE_CONTACT_RULE(3, "手机通讯录"),
        MOBILE_CALL_LIST_RULE(4, "通话记录"),
        SHORT_MESSAGE_RULE(5, "短信记录"),
        INSTALLED_LOAN_APP(6, "贷款App"),
        DEVICE_RULE(7, "设备规则"),
        MOBILE_CARRIER(8, "运营商"),
        MOBILE_SYSTEM(9, "手机系统");


        RuleTypeEnum(Integer typeValue, String typeDesc) {
            this.typeValue = typeValue;
            this.typeDesc = typeDesc;
        }

        private Integer typeValue;
        private String typeDesc;
    }
}
