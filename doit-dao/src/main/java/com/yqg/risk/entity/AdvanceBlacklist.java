package com.yqg.risk.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("advanceBlacklist")
public class AdvanceBlacklist  extends BaseEntity {
    private String recommendation;
    private String userUuid;
    private String orderNo;
}
