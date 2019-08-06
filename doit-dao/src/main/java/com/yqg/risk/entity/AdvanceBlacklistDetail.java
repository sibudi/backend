package com.yqg.risk.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Table("advanceBlacklistDetail")
public class AdvanceBlacklistDetail extends BaseEntity {
    private String userUuid;
    private String orderNo;
    private String eventTime;
    private String hitReason;
    private String productType;
    private String reasonCode;
}
