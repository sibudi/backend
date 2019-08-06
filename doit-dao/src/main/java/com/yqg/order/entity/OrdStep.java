package com.yqg.order.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Didit Dwianto on 2017/11/25.
 */
@Data
@Table("ordStepHistory")
public class OrdStep extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -5530735166848159146L;

    private String orderId;
    private int step;
    private Date stepChangeTime;
    private String userUuid;
    private String productUuid;
}