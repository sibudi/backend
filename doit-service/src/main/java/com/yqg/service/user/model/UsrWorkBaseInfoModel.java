package com.yqg.service.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/11/25.
 */
@Data
public class UsrWorkBaseInfoModel {
    private String userUuid = "";
    private String email = "";
    private String academic = "";
    private int maritalStatus;
    private String birthday = "";
    private String religion = "";
    private String motherName = "";
    private int childrenAmount;
    private String province = "";
    private String city = "";
    private String bigDirect = "";
    private String smallDirect = "";
    private String detailed = "";
    private String borrowUse = "";
    private String birthProvince= "";//
    private String birthCity= "";//
    private String birthBigDirect= "";//
    private String birthSmallDirect= "";//

    //税卡
    private String npwp;
    //保险卡
    private String insuranceCardPhoto;
    //whatsapp账号
    private String whatsappAccount;

    //家庭卡url
    private String kkCardPhoto;
}