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
    private Integer fromCallRecord; //1: yes, 0: no
    private Integer isRelative;//1: yes, 0: no
    private Integer isConfirmed;//Whether the user confirms 1: yes, 0: no
    private Integer orderSequence; //Select sort
}
