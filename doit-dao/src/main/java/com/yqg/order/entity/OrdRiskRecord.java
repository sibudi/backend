package com.yqg.order.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Didit Dwianto on 2017/12/18.
 */
@Data
@Table("ordRiskRecord")
public class OrdRiskRecord extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 2363232773708618063L;

    private String userUuid;

    private String orderNo;

    private Integer ruleType;

    private String ruleDetailType;

    private String ruleDesc;

    private String ruleRealValue;
}
