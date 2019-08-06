package com.yqg.system.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
@Table("sysAppH5")
public class SysAppH5 extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 7921817676234985895L;

    private String urlDesc;

    private String urlKey;

    private String urlValue;

    private String uuid;
}
