package com.yqg.system.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
@Table("sysDicItem")
public class SysDicItem extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -983865452938629030L;
    private String dicId;//
    private String dicItemValue;//
    private String dicItemName;//
    private String language;//

}
