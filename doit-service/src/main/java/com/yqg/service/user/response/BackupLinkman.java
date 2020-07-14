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
    private Integer fromCallRecord; //1: yes, 0: no
    private Integer isRelative;//1: yes, 0: no
    private Integer isConfirmed;//Whether the user confirms 1: yes, 0: no


}
