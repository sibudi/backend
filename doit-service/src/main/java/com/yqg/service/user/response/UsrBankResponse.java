package com.yqg.service.user.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户银行响应类
 * Created by Didit Dwianto on 2017/11/27.
 */
@Data
public class UsrBankResponse implements Serializable {
    private static final long serialVersionUID = -6661595538542704705L;

    private String bankCode;//
    private String bankNumberNo;//

    private String bankorder;//
    private String isRecent;//
}
