package com.yqg.service.third.izi.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by wanghuaizhou on 2018/12/5.
 */
@Data
public class IziResponse {

    private String status;
    private Object message;


    @Getter
    @Setter
    public static class IziIdentityCheckDetail {
        private String id;
        private String name;
        private Boolean match;
    }

    @Getter
    @Setter
    public static class IziWhatsAppDetailResponse {
        private String number;
        private String whatsapp; //是否开通，yes，no，checking
        private String status_update; //状态更新时间
        private String signature;//签名
        private String business_user;// yes or no 是否是企业账户
        private String avatar;//yes or no 是否有头像
    }
}
