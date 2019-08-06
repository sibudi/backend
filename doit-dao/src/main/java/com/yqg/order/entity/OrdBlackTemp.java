package com.yqg.order.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Didit Dwianto on 2017/12/8.
 */
@Data
@Table("ordBlackTemp")
public class OrdBlackTemp extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1864760633723378966L;

    private String userUuid;//

    private String orderNo;//
    private String ruleValue;

    private String ruleHitNo;//

    private String ruleRealValue;//

    private String responseMessage;//
}
