package com.yqg.service.signcontract.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignInfoResponse {
    private String step;
    private String toUrlData; //加载的页面base64(html) 数据
    private Long checkInterval;//每隔多久检查一次状态(单位秒)

    public enum SignStepEnum{
        TO_ACTIVATION, //进入激活页面
        TO_SIGN_CONTRACT,//进入签约页面
        TO_CONTRACT_WAITING //进入等待签约页面(待数据发送)
    }
}
