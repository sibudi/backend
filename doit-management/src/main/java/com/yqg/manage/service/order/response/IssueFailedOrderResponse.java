package com.yqg.manage.service.order.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
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
public class IssueFailedOrderResponse extends OrderBaseResponse {

    @ApiModelProperty(value = "失败时间")
    private Date failedTime;

    @ApiModelProperty(value = "银行名称")
    private String bankCardName;//银行名称

    @ApiModelProperty("银行卡尾号")
    private String shortBankNumber;//银行卡尾号

    private String userBankUuid;
    @JsonIgnore
    private String bankNumberNo;//银行卡号

    @ApiModelProperty("失败原因")
    private String errorMessage;//错误原因

//    @ApiModelProperty("???????:???+4???")
//    private String bankCardInfoDesc;

}
