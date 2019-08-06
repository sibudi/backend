package com.yqg.system.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseCondition;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * Created by Didit Dwianto on 2017/12/2.
 */
@Data
@Table("sysShareDict")
public class SysShareDict extends BaseCondition implements Serializable {

    private static final long serialVersionUID = -1126728806712071646L;

    private Integer id;

    private Integer disabled;

    private String uuid;

    private Date createTime;

    private Date  updateTime;

    private String shareTitle;

    private String shareContent;

    private String shareImageUrl;

    private String shareUrl;
}
