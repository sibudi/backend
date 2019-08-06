package com.yqg.service.p2p.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class P2PResponseDetail {
    //还款成功返回明细
    @Getter
    @Setter
    public static class RepaySuccessStatusDetail{
        private String status;
        private String paymentcode;
        private String amountActual;
        private String depositStatus;
        private String externalId;
        private String transactionId;
        private String depositMethod;
        private String depositChannel;
    }
}
