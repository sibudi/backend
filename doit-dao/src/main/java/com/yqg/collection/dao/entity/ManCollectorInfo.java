package com.yqg.collection.dao.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 *
 ****/

@Getter
@Setter
@Table(value = "collectorInfo")
public class ManCollectorInfo extends BaseEntity implements Serializable {
    private Integer postId;//岗位id
    private Integer userId;//催收人员id
    private String realName;//催收人员姓名

    /**
     * 催收人员是否休息 0，否；1 是
     */
    private Integer rest;

    /**
     * 人员来源 0:催收人员分配; 1,质检人员分配
     */
    private Integer sourceType;

}
