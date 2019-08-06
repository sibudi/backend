package com.yqg.service.user.request;

import com.yqg.common.models.BaseRequest;
import com.yqg.service.user.response.BackupLinkman;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BackupLinkmanRequest extends BaseRequest {
    
    private List<BackupLinkman> backupLinkmanList;
}
