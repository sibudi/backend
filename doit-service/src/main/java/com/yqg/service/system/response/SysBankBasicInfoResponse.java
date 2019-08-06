package com.yqg.service.system.response;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Didit Dwianto on 2017/12/3.
 */
@Data
public class SysBankBasicInfoResponse implements Serializable {
    private static final long serialVersionUID = 8383129803271507607L;
    private String bankName;//
    private String bankCode;//
}
