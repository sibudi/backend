/*
 * Copyright (c) 2017-2018 , Inc. All Rights Reserved.
 */
package com.yqg.service.partner.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.models.BaseRequest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TransactionDataRequest {

    @ApiModelProperty(value = "")
    @JsonProperty("gross_value")
    private String gross_value;
    @ApiModelProperty(value = "")
    @JsonProperty("freq_transaction_volume")
    private String freq_transaction_volume;
    @ApiModelProperty(value = "")
    @JsonProperty("store_type")
    private String store_type;
    @JsonProperty("verified")
    private Integer verified;
    @JsonProperty("fraud_indication_history")
    private Integer fraud_indication_history;
    @JsonProperty("others")
    private List<TransactionDataOtherRequest> others;
    
}