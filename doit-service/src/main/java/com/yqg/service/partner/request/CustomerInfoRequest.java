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

import java.util.Date;

import javax.validation.constraints.NotNull;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CustomerInfoRequest {

    @ApiModelProperty(value = "")
    @JsonProperty("mobile_no")
    private String mobileNo;
    @ApiModelProperty(value = "")
    @JsonProperty("email_address")
    private String emailAddress;
    @ApiModelProperty(value = "")
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("national_id")
    private String nationalId;
    @JsonProperty("tax_number")
    private String taxNumber;
    @JsonProperty("gender")
    private Integer gender;
    @JsonProperty("date_of_birth")
    private Date dateOfBirth;
    @JsonProperty("place_of_birth")
    private String placeOfBirth;
    @JsonProperty("religion")
    private String religion;
    @JsonProperty("marital_status")
    private String maritalStatus;
    @JsonProperty("last_education")
    private String lastEducation;
    @JsonProperty("mother_maiden_name")
    private String motherMaidenName;
    @JsonProperty("loan_purpose")
    private String loanPurpose;
    @JsonProperty("no_of_children")
    private String noOfChildren;
    @JsonProperty("whatsapp_no")
    private String whatsappNo;
    @JsonProperty("banks_info")
    private List<BankInfoRequest> banksInfo;
    @JsonProperty("address")
    private AddressRequest address;
    @JsonProperty("work_info")
    private WorkInfoRequest workInfo;
    @JsonProperty("other_contacts")
    private List<OtherContactRequest> otherContacts;
    @JsonProperty("attachments")
    private List<AttachmentRequest> attachments;
    @JsonProperty("device_info")
    private DeviceInfoRequest deviceInfo;
    @JsonProperty("transaction_data")
    private TransactionDataRequest transactionData;
}