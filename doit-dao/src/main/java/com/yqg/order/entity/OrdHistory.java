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
@Table("ordHistory")
public class OrdHistory extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1231867859027782937L;

    private String orderId;

    private Integer status;

    private Date statusChangeTime;

    private String statusChangePerson;

    private String userUuid;

    private String productUuid;
}
