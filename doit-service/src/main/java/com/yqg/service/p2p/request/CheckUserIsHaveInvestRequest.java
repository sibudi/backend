package com.yqg.service.p2p.request;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/9/7.
 */
@Data
public class CheckUserIsHaveInvestRequest extends P2PInvokeBaseParam {

    private String idCardNo ; // 身份证号
    private String mobileNumber ; // 手机号  不能是08 628开头  必须转换成8xxx
}
