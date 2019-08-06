package com.yqg.manage.entity.system;


import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseCondition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Jacob
 */
@Table("manOrderRemark")
@Data
@ApiModel
public class ManOrderRemark extends BaseCondition implements Serializable {
    private static final long serialVersionUID = -2283877144674010417L;

    private Integer id;

    private String uuid;

    private Date createTime;

    private Integer createUser;

    private Date updateTime;

    private Integer updateUser;

    private String orderNo;

    private String remark;

    @ApiModelProperty(value = "订单标签")
    private Integer orderTag;
    @ApiModelProperty(value = "提醒时间")
    private Date alertTime;
    @ApiModelProperty(value = "开始时间")
    private Date startTime;
    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "时长")
    private String burningTime;

    private Integer disabled;

    private Integer type;

    @ApiModelProperty(value = "电话审核结果")
    private String teleReviewResult;
    @ApiModelProperty(value = "工作年限 ManWorkYearEnum")
    private Integer workYear;
    @ApiModelProperty(value = "操作类型 (1 稍后再拨， 2 通过，3 无法审核，4 拒绝)")
    private Integer operationType;
    @ApiModelProperty(value = "电核操作类型（1 公司未接通 2 公司接通 3 第一联系人未接通 4 第一联系人接通 5 第二联系人未接通 6 第二联系人接通）")
    private Integer teleReviewOperationType;
    @ApiModelProperty(value = "电核结果类型（用于页面回显）")
    private Integer teleReviewResultType;
    @ApiModelProperty(value = "常用联系人姓名")
    private String realName;
    @ApiModelProperty(value = "常用联系人电话")
    private String mobile;
    @ApiModelProperty(value = "拒绝原因描述")
    private String description;
    @ApiModelProperty(value = "承诺还款时间")
    private Date promiseRepaymentTime;

}
