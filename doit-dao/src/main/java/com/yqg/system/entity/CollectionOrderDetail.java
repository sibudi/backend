package com.yqg.system.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Getter
@Setter
@Table("collectionOrderDetail")
public class CollectionOrderDetail extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 9095619665726219027L;

    private String orderUUID;
    private Integer outsourceId;
    private Integer orderTag;
    private Date assignedTime;
    private Integer isTest;
    private Integer subOutSourceId;

    private Date promiseRepaymentTime;
}
