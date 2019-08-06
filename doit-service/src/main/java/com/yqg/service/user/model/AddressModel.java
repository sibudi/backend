package com.yqg.service.user.model;

import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/11/25.
 */
@Data
public class AddressModel {
    private String userUuid = "";
    private int addressType;
    private String province = "";
    private String city = "";
    private String bigDirect = "";
    private String smallDirect = "";
    private String detailed = "";
    private String lbsX = "";
    private String lbsY = "";

}