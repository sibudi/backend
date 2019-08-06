package com.yqg.service.user.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 卡bin校验响应
 * Created by Didit Dwianto on 2017/11/26.
 */
@Data
public class UsrBankCardBinResponse implements Serializable {
    private static final long serialVersionUID = -988630437995766952L;
//    private String code;
//    private String bankCode;
//    private String bankCardNumber;
//    private String bankHolderName;
//    private UserBankCardBinEnum bankCardVerifyStatus;
//    private String errorMessage;
    private String state;
}
