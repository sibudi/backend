package com.yqg.system.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Didit Dwianto on 2018/2/2.
 */
@Data
@Table("sysInstalledApps")
public class SysCheakApps extends BaseEntity implements Serializable{

    private static final long serialVersionUID = 7341080037132611464L;

    private Integer status;

    private String applicationWorkspace;

    private String workspace;

    private String applications;

    private String bundleIdentifier;

    private String bundleVersion;

    private String localizedName;

}
