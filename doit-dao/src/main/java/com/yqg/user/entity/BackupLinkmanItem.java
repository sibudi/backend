package com.yqg.user.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("backupLinkmanItem")
public class BackupLinkmanItem extends BaseEntity implements Serializable {
    private String orderNo;
    private String userUuid;
    private String linkmanName;
    private String linkmanNumber;
    private Integer fromCallRecord; //1:是，0:否
    private Integer isRelative;//1:是0:否
    private Integer isConfirmed;//用户是否确认 1:是，0：否
    private Integer orderSequence; //选择排序
}
