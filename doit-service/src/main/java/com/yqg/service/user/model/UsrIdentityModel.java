package com.yqg.service.user.model;

import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/11/25.
 */
@Data
public class UsrIdentityModel {
    private String realName = "";
    private String idCardNo = "";
    private Integer sex;
    private String idCardUrl = "";
    private String handIdCardUrl = "";
    private String insuranceCardUrl = "";
    /**
     */
    private String facePhotoUrl = "";

}