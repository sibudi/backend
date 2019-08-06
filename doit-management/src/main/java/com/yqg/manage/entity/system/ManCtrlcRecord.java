package com.yqg.manage.entity.system;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Author: tonggen
 * Date: 2019/5/14
 * time: 10:51 AM
 */
@Data
@Table("manCtrlcRecord")
public class ManCtrlcRecord extends BaseEntity implements Serializable{

    private String orderNo;

    private String userUuid;

    private Integer operator;

    /**
     * 复制的内容
     */
    private String content;
}
