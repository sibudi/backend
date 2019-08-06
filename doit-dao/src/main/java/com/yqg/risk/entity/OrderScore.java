package com.yqg.risk.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Table("orderScore")
public class OrderScore  extends BaseEntity {
    private String userUuid;
    private String orderNo;
    private BigDecimal totalScore;
    private Integer version;
    private Integer rulePass;// 1: pass 0: reject
    private Integer scorePass; //1:pass 0: reject
    private Integer manualReview;// 1：需人工审核 0：不需要
    private String modelName;//模型对应产品(or 模型名称)
}
