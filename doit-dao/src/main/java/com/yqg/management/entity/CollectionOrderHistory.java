package com.yqg.management.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Getter
@Setter
@Table("collectionOrderHistory")
public class CollectionOrderHistory  extends BaseEntity implements Serializable {


    private static final long serialVersionUID = -5467838844239647197L;
    private String orderUUID;
    private Integer outsourceId;

    private Integer sourceType;
}
