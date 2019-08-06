package com.yqg.service.check.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Jacob
 * Date: 07/02/2018
 * Time: 5:28 PM
 */
@Data
public class OrderPayCheckResponse {
    private String code;
    private String externalId;
    private BigDecimal amount;
    private String disburseStatus;
    private String disbursementId;
    private Date lendingTime;
    private String errorCode;
    private String errorMessage;


}
