package com.yqg.management.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/*****
 * @Author super
 * created at ${date}
 *
 ****/

@Getter
@Setter
@Table("infinityExtnumber")
public class InfinityExtnumberEntity extends BaseEntity implements Serializable {

    private String extnumber;
    private String password;
    private String status;
}
