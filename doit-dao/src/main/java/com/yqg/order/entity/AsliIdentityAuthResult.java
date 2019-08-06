package com.yqg.order.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by wanghuaizhou on 2019/3/26.
 */
@Data
@Table("asliIdentityAuthResult")
public class AsliIdentityAuthResult extends BaseEntity implements Serializable {

    private String orderNo;
    private String userUuid;
    private String message ;
    private String status;
    private String error;
    private String name;
    private String birthplace;
    private String birthdate;
    private String address;

}
