package com.yqg.base.data.condition;

import lombok.Data;

import java.util.Date;

/**
 * Created by Didit Dwianto on 2018/2/26.
 */
@Data
public class BaseMongoEntity {

    private Integer disabled;

    private String uuid;

    private Date createTime;

    private Date updateTime;

    private String remark;

    private String id;

}
