package com.yqg.manage.service.collection.request;

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
public class ManCollectionRemarkRequest {

    @ApiModelProperty(value = "创建用户Id （需要每次都传）")
    @JsonProperty
    private Integer createUser;
    @ApiModelProperty(value = "更新用户Id （需要每次都传）")
    @JsonProperty
    private Integer updateUser;
    @ApiModelProperty(value = "订单编号（需要每次都传）")
    @JsonProperty
    private String orderNo;
    @ApiModelProperty(value = "备注（填写备注时传）")
    @JsonProperty
    private String remark;
    @ApiModelProperty(value = "订单标签（添加催收打标签时传）")
    @JsonProperty
    private Integer orderTag;
    @ApiModelProperty(value = "提醒时间（需要提醒时传）")
    @JsonProperty
    private String alertTime;
    @ApiModelProperty(value = "语言 1中文 2印尼文")
    @JsonProperty
    private Integer langue;
    @ApiModelProperty(value = "联系人电话")
    @JsonProperty
    private String contactMobile;
    @ApiModelProperty(value = "联系人类型")
    @JsonProperty
    private Integer contactType;
    @ApiModelProperty(value = "联系人方式")
    @JsonProperty
    private Integer contactMode;
    @ApiModelProperty(value = "联系人结果")
    @JsonProperty
    private Integer contactResult;
    @ApiModelProperty(value = "承诺还款时间")
    private String promiseRepaymentTime;

    @ApiModelProperty(value = "发薪日")
    private Integer payDay;

    @ApiModelProperty(value = "用户uuid")
    private String userUuid;

    @ApiModelProperty(value = "服务意识")
    @JsonProperty
    private Integer serviceMentality;
    @ApiModelProperty(value = "沟通能力")
    @JsonProperty
    private Integer communicationBility;
    @ApiModelProperty(value = "还款意愿")
    @JsonProperty
    private Integer repayDesire;
    @ApiModelProperty(value = "还款能力")
    @JsonProperty
    private Integer repayBility;
    @ApiModelProperty(value = "用户素质")
    @JsonProperty
    private Integer userDiathesis;

    @JsonProperty
    private Integer type;

    @Override
    public String toString() {
        return "ManCollectionRemarkRequest{" +
                "createUser=" + createUser +
                ", updateUser=" + updateUser +
                ", orderNo='" + orderNo + '\'' +
                ", remark='" + remark + '\'' +
                ", orderTag=" + orderTag +
                ", alertTime='" + alertTime + '\'' +
                ", langue=" + langue +
                ", contactType=" + contactType +
                ", contactMode=" + contactMode +
                ", contactResult=" + contactResult +
                ", promiseRepaymentTime='" + promiseRepaymentTime + '\'' +
                ", payDay=" + payDay +
                ", userUuid='" + userUuid + '\'' +
                '}';
    }
}
