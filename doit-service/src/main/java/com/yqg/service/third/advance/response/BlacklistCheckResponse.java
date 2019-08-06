package com.yqg.service.third.advance.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/***
 * {"code":"SUCCESS","message":"OK","data":{"defaultListResult":[],"recommendation":"PASS"},"extra":null,"transactionId":"dd5068dd7adf2fa8","pricingStrategy":"FREE"}
 * {"code":"INVALID_ID_NUMBER","message":"Invalid ID number, please check the NIK format","data":null,"extra":null,"transactionId":"c97d803db87cb449","pricingStrategy":"FREE"}
 *
 */

@Getter
@Setter
public class BlacklistCheckResponse {
    private String code;
    private String message;
    private DataDetail data;
    private String transactionId;
    private String pricingStrategy;
    private Object extra;

    @Getter
    @Setter
    public static class DataDetail {
        private String recommendation;

        private List<EventDetail> defaultListResult;

        @Getter
        @Setter
        public static class EventDetail{
            private String eventTime;
            private String hitReason;
            private String productType;
            private String reasonCode;
        }
    }

    public boolean isHitReject(){
        if(this.getData()!=null && "REJECT".equalsIgnoreCase(this.getData().getRecommendation())){
            return true;
        }
        return false;
    }
}
