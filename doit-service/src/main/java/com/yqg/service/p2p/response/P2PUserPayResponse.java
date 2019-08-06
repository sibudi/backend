package com.yqg.service.p2p.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class P2PUserPayResponse extends P2PResponse {

    private DataDetail data;

    @Getter
    @Setter
    public static class DataDetail{
        private String paymentCode;
        private String depositChannel;
        private String externalId;
        private String depositStatus;
        private String transactionId;
    }
}
