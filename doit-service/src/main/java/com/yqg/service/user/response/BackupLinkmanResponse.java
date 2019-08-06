package com.yqg.service.user.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BackupLinkmanResponse {

    private int minSelectedLimit = 10;//最小必须选择的联系人
    private List<BackupLinkman> backupLinkmanList = new ArrayList<>();
}
