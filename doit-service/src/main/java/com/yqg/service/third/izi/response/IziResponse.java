package com.yqg.service.third.izi.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.user.entity.UsrUser;

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

    @Getter
    @Setter
    public static class IziBlackListResponse {
        private String status; //OK, INVALID_ID_NUMBER, INVALID_PHONE_NUMBER, NOT_FOUND_MD5, RETRY_LATER
        private String message; //REJECT, NEEDS_VERIFICATION, PASS
    }

    @Getter
    @Setter
    public static class IziMultiInquiriesV1Response {

        private String status;
        private IziMultiInquiriesV1Message message;

        public boolean isHitReject(UsrUser user) {
            if (user.getSex() == 1) {
                return message.get_07d() != null && message.get_07d() >= 11;
            } else {
                return message.get_07d() != null && message.get_07d() >= 10;
            }

            // return true;
        }
    }

    @Getter
    @Setter
    private static class IziMultiInquiriesV1Message{
        @JsonProperty("07d")
        private Integer _07d;

        @JsonProperty("14d")
        private Integer _14d;

        @JsonProperty("21d")
        private Integer _21d;

        @JsonProperty("30d")
        private Integer _30d;

        @JsonProperty("60d")
        private Integer _60d;

        @JsonProperty("90d")
        private Integer _90d;

        private Integer total;
    }
}
