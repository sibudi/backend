package com.yqg.activity.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Features:
 * Created by huwei on 18.8.15.
 */
@Data
@Table("usrInviteRecord")
public class UsrInviteRecord extends BaseEntity {
    private String userUuid;
    private String mobileNumber;
    private String invitedUserUuid;//被邀请人uuid
    private String invitedMobileNumber;//被邀请人手机号
    private Integer friendSource;//被邀请人来源
    private Date regTime;//被邀请人注册时间
    private Integer type;//被邀请人步骤
    private Integer status;//佣金发放状态
    private BigDecimal amount;//佣金金额
}
