package com.yqg.user.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/6/8.
 */
@Data
@Table("loanUser")
public class LoanUser extends BaseEntity {

    private Integer isSend;//是否发送过短信 0 未发送 1已发送
    private String userUuid;//
    private String userMobile;//
}
