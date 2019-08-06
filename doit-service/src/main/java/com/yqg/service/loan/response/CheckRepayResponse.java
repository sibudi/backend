package com.yqg.service.loan.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckRepayResponse {
    String code;

    String depositStatus;

    String errorMessage;

    String paymentCode;

    String externalId;

    String transactionId;

    String depositMethod;

    String depositChannel;

    String amount;
}
