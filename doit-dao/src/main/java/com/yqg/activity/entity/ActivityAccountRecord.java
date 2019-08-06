package com.yqg.activity.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Features:
 * Created by huwei on 18.8.15.
 */
@Data
@Table("activityAccountRecord")
public class ActivityAccountRecord extends BaseEntity {
    private String userUuid;//用户id
    private BigDecimal balance;//账户当前结余
    private BigDecimal perBalance;//账户之前结余
    private BigDecimal amount;//当前交易额
    private Integer channel;//交易渠道 1-银行卡 2-gopay
    private Integer type;//交易类型 交易类型 1-一级好友佣金 2-二级好友佣金 3-提现 4-提现锁定 5-提现失败 6提现失败退回 7手动处理
    private String amountSource;//佣金来源
    private String caseoutAccount;//提现账户
    private String goPayUserName;//gopay账户姓名
    private String loanStatus;//放款状态  0 待放款 1放款中 2放款成功 3放款失败
}
