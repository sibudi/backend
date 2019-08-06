package com.yqg.service.user.model;

import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/11/25.
 */
@Data
public class AttachmentModel {
    private String userUuid;
    private Integer attachmentType;
    private String attachmentUrl;
    private String attachmentSaveZone;
    private String attachmentSavePath;
}