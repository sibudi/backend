package com.yqg.order.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Didit Dwianto on 2017/12/8.
 */
@Data
@Table("ordBlack")
public class OrdBlack extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 8476718482713508766L;

    private String userUuid;

    private String orderNo;//

    private String ruleValue;//

    private String ruleHitNo;//

    private String ruleRealValue;//

    private String responseMessage;//

    private Integer ruleRejectDay;//
}
