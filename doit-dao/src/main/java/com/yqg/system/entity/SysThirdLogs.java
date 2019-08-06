package com.yqg.system.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * ???
 * Created by Didit Dwianto on 2017/11/26.
 */
@Data
@Table("sysThirdLogs")
public class SysThirdLogs extends BaseEntity implements Serializable{
    private static final long serialVersionUID = -2512079802123404237L;
    private String orderNo;// ???
    private String userUuid;// ??id
    private Integer thirdType;// ?????
    private Integer logType;// ???????????????
    private String request;// ??(1??????2?advanc?3????4??????5??bin??)
    private String response;// ??
    private String timeUsed;//??????
}
