package com.yqg.service.order.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.models.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * ????model
 * Created by Didit Dwianto on 2017/11/25.
 */
@Data
public class OrdRequest extends BaseRequest implements Serializable {
    private static final long serialVersionUID = -3026215436454860006L;
    private String productUuid;//??ID
    private String detailed;//???????????????


    @ApiModelProperty(value = "????")
    @JsonProperty
    private String deviceType;

    @ApiModelProperty(value = "????")
    @JsonProperty
    private String deviceName;

    @ApiModelProperty(value = "????")
    @JsonProperty
    private String phoneBrand;

    @ApiModelProperty(value = "???")
    @JsonProperty
    private String totalMemory;

    @ApiModelProperty(value = "????")
    @JsonProperty
    private String remainMemory;

    @ApiModelProperty(value = "?????")
    @JsonProperty
    private String totalSpace;

    @ApiModelProperty(value = "??????")
    @JsonProperty
    private String remainSpace;

    @ApiModelProperty(value = "IMEI")
    @JsonProperty
    private String IMEI;

    @ApiModelProperty(value = "IMSI")
    @JsonProperty
    private String IMSI;

    @ApiModelProperty(value = "SIM???")
    @JsonProperty
    private String SimNumber;

    @ApiModelProperty(value = "CPU??")
    @JsonProperty
    private String cpuType;

    // ????????
    private String lastPowerOnTime;

    // DNS
    private String dnsStr;

    // ????
    private String isRoot;

    // ?????
    private String memoryCardCapacity;

    // wifi??
    private String wifiList;

    // ????
    private String mobileLanguage;

    // ??????
    private String isSimulator;

    // ????
    private String battery;

    // ????
    private String pictureNumber;

    private String province;// ?
    private String city;//   ?
    private String bigDirect;//  ??
    private String smallDirect;//  ??

    private Integer orderType;  // 1 cashcash订单 2 cheetah订单

    private String orderNo;//订单号


}
