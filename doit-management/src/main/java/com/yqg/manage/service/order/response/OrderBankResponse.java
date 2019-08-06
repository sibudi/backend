package com.yqg.manage.service.order.response;

import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.manage.service.order.request.OrderSearchRequest.OrderChannelEnum;
import com.yqg.manage.service.order.request.OrderSearchRequest.UserRoleEnum;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author Jacob
 */

@Data
public class OrderBankResponse {
    /**
     * 开户行
     */
    private String bankCode;
    /**
     * 卡号
     */
    private String bankNumberNo;
    /**
     * 持卡人姓名
     */
    private String bankCardName;
    /**
     * 银行卡状态(0=未验证，1=待验证,2=成功,3=失败)
     */
    private Integer status;
}
