package com.yqg.service.user.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2018/1/2.
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheakSmsBalanceResponse {

    private String code;

    private String message;

    private String expireDate;

    private String balance;

    private String smsChannel;
}
