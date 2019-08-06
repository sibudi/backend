package com.yqg.manage.entity.collection;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseCondition;
import com.yqg.base.data.condition.BaseEntity;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

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

    private Integer sourceType;
    private Integer checkResult;
    private Integer voiceCheckResult;//语音质检结果
}
