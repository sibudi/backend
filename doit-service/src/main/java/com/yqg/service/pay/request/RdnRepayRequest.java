package com.yqg.service.pay.request;

import com.yqg.common.models.BaseRequest;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Created by arief.halim on 2020-01-21.
 */
@Data
public class RdnRepayRequest {

    private String parentOrderNo;
    private String orderNo;
    private BigDecimal amount;
    private BigDecimal interest;
    private BigDecimal overdueFee;
    private BigDecimal penalty; //ahalim: Currently this value contains penaltyfee without limit
    private OrderType orderType;

    public enum OrderType{
        //Refer to ordTypeEnum.java at table doit.ordOrder
        NORMAL("0"),
        NORMAL_EXTENDED("1"),
        EXTEND("2"),
        INSTALLMENT("3");

        private String code;
        
        private OrderType(String code){
            this.code = code;
        }
        
        public String getCode() {
            return this.code;
        }
        public static OrderType fromString(String text) {
            for (OrderType ot : OrderType.values()) {
                if (ot.code.equalsIgnoreCase(text)) {
                    return ot;
                }
            }
            return null;
        }
    }
}
