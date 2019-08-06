package com.yqg.system.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
@Table("sysDic")
public class SysDic extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 9161948923491624377L;
    private String dicName;//
    private String dicCode;//

}
