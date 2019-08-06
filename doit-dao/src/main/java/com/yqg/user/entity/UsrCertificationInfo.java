package com.yqg.user.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
@Table("usrCertificationInfo")
public class UsrCertificationInfo extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 7009714556549454150L;

    private String userUuid;
    private Integer certificationType;
    private String certificationData;
    private Integer certificationResult;
}