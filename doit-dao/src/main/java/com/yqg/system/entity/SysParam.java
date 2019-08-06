package com.yqg.system.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Didit Dwianto on 2017/11/25.
 */
@Data
@Table("sysParam")
public class SysParam extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -5623205811143124316L;

    private String description;

    private String sysKey;

    private String sysValue;

}
