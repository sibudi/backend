package com.yqg.system.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Author: tonggen
 * Date: 2019/6/20
 * time: 2:26 PM
 */
@Getter
@Setter
@Table("collectionOrderHistory")
public class ManCollectionOrderHistory  extends BaseEntity implements Serializable {


    private static final long serialVersionUID = -5467838844239647197L;
    private String orderUUID;
    private Integer outsourceId;

    private Integer sourceType;
}
