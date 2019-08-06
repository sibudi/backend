package com.yqg.user.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Jacob on 2018/4/24.
 */
@Data
@Table("usrContractInfo")
public class UsrContractInfo extends BaseEntity implements Serializable {

    private String name;
    private String phone;
    private String contactTime;
    private String orderNo;
}
