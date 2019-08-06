package com.yqg.risk.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("advanceMultiPlatform")
public class AdvanceMultiPlatform extends BaseEntity {
    private String userUuid;
    private String orderNo;
    private Integer tp1To7D;
    private Integer tp1To14D;
    private Integer tp1To21D;
    private Integer tp1To30D;
    private Integer tp1To60D;
    private Integer tp1To90D;
    private Integer mqc1H;
    private Integer mqc3H;
    private Integer mqc6H;
    private Integer mqc12H;
    private Integer mqc24H;
}
