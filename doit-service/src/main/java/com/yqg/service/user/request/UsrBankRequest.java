package com.yqg.service.user.request;

import com.yqg.common.models.BaseRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户银行卡请求model
 * Created by Didit Dwianto on 2017/11/26.
 */
@Data
public class UsrBankRequest extends BaseRequest implements Serializable {
    private static final long serialVersionUID = 8425733672768388160L;
    private String bankCode;//
    private String bankNumberNo;//
    private String bankCardName;//
    private String orderNo;
    private Integer thirdType = 0;// 第三方接口标识（默认是0=uang2，1=cashcash, 2=cheetah）

    private String blackBox; //同盾设备指纹
}
