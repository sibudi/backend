package com.yqg.service.externalChannel.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/7
 * @Email zengxiangcai@yishufu.com
 * 可贷款用户、复贷简化流程检查参数
 ****/

@Getter
@Setter
public class Cash2LoanableCheckParam {
    private String mobile;

//    @JsonProperty("user_name")
//    private String userName;

    //  当用户首次借款时，无法获取用户身份号码
    @JsonProperty(value = "user_idcard")
    private String idcard;

    @JsonProperty(value = "product_id")
    private int productId;
}
