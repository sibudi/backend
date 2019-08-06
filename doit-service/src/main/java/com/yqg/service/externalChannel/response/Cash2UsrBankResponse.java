package com.yqg.service.externalChannel.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by luhong on 2018/3/13.
 */
@Data
public class Cash2UsrBankResponse {
    @JsonProperty(value = "order_no")
    private String orderNo;
    @JsonProperty(value = "bank_card_list")
    private List<Cash2BankResponse> bankCardList;

    @Data
    public static class Cash2BankResponse {
        @JsonProperty(value = "open_bank")
        private String openBank;
        @JsonProperty(value = "bank_card")
        private String bankCard;
        @JsonProperty(value = "extra_info")
        private String extraInfo;
    }


}
