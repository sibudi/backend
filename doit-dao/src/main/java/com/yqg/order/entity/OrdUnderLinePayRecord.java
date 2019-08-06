package com.yqg.order.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by wanghuaizhou on 2018/6/8.
 */
@Data
@Table("ordUnderLinePayRecord")
public class OrdUnderLinePayRecord extends BaseEntity implements Serializable {

    private String userUuid;
    private String orderNo;
    private String actualRepayAmout;

    private Date actualRepayTime;

}
