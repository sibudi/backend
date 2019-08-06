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
@Table("activityAccount")
public class ActivityAccount extends BaseEntity {
    private String userUuid;//用户uuid
    private BigDecimal balance;//账户结余
    private BigDecimal lockedbalance;//当前锁定金额

}
