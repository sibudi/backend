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
public class ManOrderListSearchResquest {
    @ApiModelProperty(value = "订单编号")
    @JsonProperty
    private String uuid;
    @ApiModelProperty(value = "姓名")
    @JsonProperty
    private String realName;
    @ApiModelProperty(value = "手机号")
    @JsonProperty
    private String mobile;
    @ApiModelProperty(value = "订单状态")
    @JsonProperty
    private Integer status ;
    @ApiModelProperty(value = "用户uuid集合")
    @JsonProperty
    private String uuidString ;
    @ApiModelProperty(value = "页数")
    @JsonProperty
    private Integer pageNo = 1;
    @ApiModelProperty(value = "分页大小")
    @JsonProperty
    private Integer pageSize = 10;
    @ApiModelProperty(value = "逾期天数极小值")
    @JsonProperty
    private Integer overdueDayMin;
    @ApiModelProperty(value = "逾期天数极大值")
    @JsonProperty
    private Integer overdueDayMax;
    @ApiModelProperty(value = "订单标签")
    @JsonProperty
    private Integer orderTag;
    @ApiModelProperty(value = "委外订单与第三方催收账号关联id")
    @JsonProperty
    private Integer outsourceId;
    @ApiModelProperty(value = "是否分配给第三方催收公司")
    @JsonProperty
    private Integer thirdDistribute;
    @ApiModelProperty(value = "时间")
    @JsonProperty
    private String time;
    @ApiModelProperty(value = "借款次数")
    @JsonProperty
    private Integer borrowingCount;
    @ApiModelProperty(value = "关联id集合")
    @JsonProperty
    private String outsourceIdString;
    @ApiModelProperty(value = "订单状态集合")
    @JsonProperty
    private String statusString;
    @ApiModelProperty(value = "用户roleId")
    @JsonProperty
    private String roleId;
    @ApiModelProperty(value = "还款时间极大值")
    @JsonProperty
    private String repayDayMax;
    @ApiModelProperty(value = "还款时间极小值")
    @JsonProperty
    private String repayDayMin;
    @ApiModelProperty(value = "渠道号")
    @JsonProperty
    private Integer channel;
    @ApiModelProperty(value = "是否测试订单")
    @JsonProperty
    private Integer isTestOrder;
    @ApiModelProperty(value = "用户名")
    @JsonProperty
    private String userName;
    @ApiModelProperty(value = "支付通道状态")
    @JsonProperty
    private Integer payStatus;
    @ApiModelProperty(value = "复借次数最小值")
    @JsonProperty
    private Integer repeTitionsMin;
    @ApiModelProperty(value = "复借次数最大值")
    @JsonProperty
    private Integer repeTitionsMax;
    @ApiModelProperty(value = "初审人员")
    @JsonProperty
    private String firstCheck;
    @ApiModelProperty(value = "复审人员")
    @JsonProperty
    private String secondCheck;
    @ApiModelProperty(value = "初审人员(已完成)")
    @JsonProperty
    private Integer firstCheckOr;
    @ApiModelProperty(value = "复审人员(已完成)")
    @JsonProperty
    private Integer secondCheckOr;
    @ApiModelProperty(value = "申请日期开始时间")
    @JsonProperty
    private String createBeginTime;
    @ApiModelProperty(value = "申请日期结束时间")
    @JsonProperty
    private String createEndTime;
    @ApiModelProperty(value = "申请期限")
    @JsonProperty
    private Integer borrowingTerm;

    @ApiModelProperty(value = "1中文 2印尼文")
    @JsonProperty
    private Integer langue;

}
