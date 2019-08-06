package com.yqg.service.user.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BackupLinkman {
    private Integer id;
    private String linkmanName;
    private String linkmanNumber;
    private Integer fromCallRecord; //1:是，0:否
    private Integer isRelative;//1:是0:否
    private Integer isConfirmed;//用户是否确认 1:是，0：否


}
