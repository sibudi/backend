package com.yqg.management.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yqg.base.data.annotations.Column;
import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;
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
@Data
@Table("InfinityRecodeFile")
public class InfinityRecodeFileEntity extends BaseEntity implements Serializable {

    @Column("filename")
    private String recordfilename;
    private String downUrl;
    private String downStatus;
    private String fileAddress;
}
