package com.yqg.service.externalChannel.request;

import lombok.Data;

/**
 * Author: tonggen
 * Date: 2019/1/4
 * time: 11:30 AM
 */
@Data
public class CheetahOrdInfoEditRequest{


    private OrderInfoBean orderInfo;
    private UserInfoBean userInfo;
    private BankInfoBean bankInfo;
    @Data
    public static class OrderInfoBean {
        private String id;//订单号
    }

    @Data
    public static class UserInfoBean {

        private String fullName;//用户全名

        private String idNumber;//NIK

        private String idFrontPhoto;//身份证正面照

        private String handHeldPhoto;//手持身份证照
    }
    @Data
    public static class BankInfoBean {
        private String accountNumber;//账号

        private String bankCode;//绑卡开户行代码
    }


}
