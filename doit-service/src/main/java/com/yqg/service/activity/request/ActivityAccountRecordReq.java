package com.yqg.service.activity.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.models.BaseRequest;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ActivityAccountRecordReq extends BaseRequest {
    @JsonProperty
    private Integer uid;//操作人Id
    @JsonProperty
    private String uuid;//转账编号（流水号）
    @JsonProperty
    private String caseoutAccount;//Go-pay账号
    @JsonProperty
    private String caseoutAccountName;//Go-pay账户姓名
    @JsonProperty
    private String beginTime;//申请时间段
    @JsonProperty
    private String endTime;//申请时间段
//    @JsonProperty
//    private String actUserUuid;//
    @JsonProperty
    private String mobile;//
    @JsonProperty
    private String type;//交易类型 1-一级好友佣金 2-二级好友佣金 3-提现 4-提现锁定 5-提现失败退回
    @JsonProperty
    private Integer pageNo = 1;
    @JsonProperty
    private Integer pageSize = 20;
    @JsonProperty
    private String channel;//查询账户类型  1-银行卡 2-gopay
}
