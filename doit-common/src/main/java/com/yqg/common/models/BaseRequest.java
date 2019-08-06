package com.yqg.common.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BaseRequest {
    @ApiModelProperty(value = "????", required = true)
    @JsonProperty
    private String net_type;//????
    @ApiModelProperty(value = "???????", required = true)
    @JsonProperty
    private String system_version;//???????
    @ApiModelProperty(value = "?????", required = true)
    @JsonProperty
    private String client_type;//?????
    @ApiModelProperty(value = "???", required = true)
    @JsonProperty
    private String channel_sn;//??? 
    @ApiModelProperty(value = "???", required = true)
    @JsonProperty
    private String channel_name; //??? 
    @ApiModelProperty(value = "??uuid", required = true)
    @JsonProperty
    private String deviceId;//??uuid
    @ApiModelProperty(value = "?????", required = true)
    @JsonProperty
    private String client_version;//?????
    @ApiModelProperty(value = "???", required = true)
    @JsonProperty
    private String resolution;//???
    @ApiModelProperty(value = "ip??", required = true)
    @JsonProperty
    private String IPAdress;//ip??
    @ApiModelProperty(value = "??", required = true)
    @JsonProperty
    private String sign;
    @ApiModelProperty(value = "???", required = true)
    @JsonProperty
    private String timestamp;
    @ApiModelProperty(value = "session id", required = true)
    @JsonProperty
    private String sessionId;
    @ApiModelProperty(value = "?? id", required = false)
    @JsonProperty
    private String userUuid;
    @ApiModelProperty(value = "mac??")
    @JsonProperty
    private String mac;
    @ApiModelProperty(value = "wifi mac??")
    @JsonProperty
    private String wifimac;
    @ApiModelProperty(value = "LbsX ??")
    @JsonProperty
    private String lbsX;
    @ApiModelProperty(value = "LbsX ??")
    @JsonProperty
    private String lbsY;

    private String deviceSysModel;//方便定位问题添加

    private String androidId;
}
