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
@Table("manQualityCheckConfig")
public class ManQualityCheckConfig extends BaseEntity implements Serializable {


    private static final long serialVersionUID = -5467838844239647197L;
    private String title;
    private String titleInn;

    private String fineMoney;

    private Integer type;//类型：0.备注质检 1.语音质检 2.WA质检 3.二次备注质检 4.二次语音质检 5.二次WA质检
}
