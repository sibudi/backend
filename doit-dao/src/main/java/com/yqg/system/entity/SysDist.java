package com.yqg.system.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
@Table("sysDist")
public class SysDist extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 7078078683411753604L;
    private String distName;//
    private String distCode;//
    private String distLevel;//
    private String parentCode;//
    private String language;//

}
