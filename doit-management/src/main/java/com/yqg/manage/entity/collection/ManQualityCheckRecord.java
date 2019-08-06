package com.yqg.manage.entity.collection;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Getter
@Setter
@Table("manQualityCheckRecord")
public class ManQualityCheckRecord extends BaseEntity implements Serializable {


    private static final long serialVersionUID = -5467838844239647197L;
    private String orderNo;
    private Integer checkTag;

    private Integer collectorId;
    private String userUuid;

    private Integer type;//类型：0.备注质检 1.语音质检
}
