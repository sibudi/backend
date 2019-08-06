package com.yqg.service.activity.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ActivityAccountRecordResp {
    private String uuid;//转账编号 流水号
    private String mobile;
    private String caseoutAccountName;//gopay 姓名
    private String caseoutAccount;//gopay 账号
//    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+7")
    private Date createTime;//
    private String amount;//当前交易额
    private String type;//交易类型 1-一级好友佣金 2-二级好友佣金 3-提现 4-提现锁定 5-提现失败退回
    private String balance;//账户当前结余
}
