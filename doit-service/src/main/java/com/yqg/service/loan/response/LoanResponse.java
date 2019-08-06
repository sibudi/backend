package com.yqg.service.loan.response;

import lombok.Data;

@Data
public class LoanResponse {

    String code;

    String amount;

    String bankCode;

    String bankcardNumber;

    String disburseStatus;

    String errorMessage;

    String externalId;

    String transactionId;

    String errorCode;

    String disburseChannel;

}