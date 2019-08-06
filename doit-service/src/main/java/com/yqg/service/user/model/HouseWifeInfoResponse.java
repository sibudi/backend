package com.yqg.service.user.model;

import com.yqg.user.entity.UsrHouseWifeDetail;
import lombok.Data;

/**
 * Author: tonggen
 * Date: 2018/8/21
 * time: 下午7:39
 */
@Data
public class HouseWifeInfoResponse extends UsrHouseWifeDetail{

    /**
     * 工作地址
     */
    private String workAddressDetail;

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

    //保险卡
    private String insuranceCardPhoto;
    //whatsapp账号
    private String whatsappAccount;
}
