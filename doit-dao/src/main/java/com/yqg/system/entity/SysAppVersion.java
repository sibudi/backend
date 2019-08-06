package com.yqg.system.entity;


import com.yqg.base.data.condition.BaseEntity;
import com.yqg.base.data.annotations.Table;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
@Table("sysAppVersion")
public class SysAppVersion extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -4901762549587239968L;

    private Integer id;

    private String appUpdateTime;

    private Date createTime;

    private Integer disabled;

    private String downloadAddress;

    private Integer isForceUpdate;

    private String leftBtnTitle;

    private String updateContent;

    private String remark;

    private String rightBtnTitle;

    private Integer status;

    private String updateTitle;

    private Date updateTime;

    private String uuid;

    private String appVersion;

    private Integer appType;

    private Integer sysType;
}
