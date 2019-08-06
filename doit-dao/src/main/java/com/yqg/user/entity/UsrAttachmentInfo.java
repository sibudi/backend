package com.yqg.user.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
@Table("usrAttachmentInfo")
public class UsrAttachmentInfo extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 3730216543282817318L;

    private String userUuid;
    private Integer attachmentType;
    private String attachmentUrl;
    private String attachmentSaveZone;
    private String attachmentSavePath;
}