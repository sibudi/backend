package com.yqg.manage.service.order.response;

import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.StringUtils;
import lombok.Data;

/**
 * Author: tonggen
 * Date: 2019/5/9
 * time: 4:48 PM
 */
@Data
public class TwilioCallCouponResponse {

    private String orderNo;

    private String userUuid;

    private String phoneMobile;

    /**
     * 手机号码格式化
     * @param phoneNumber
     */
    public void setPhoneMobile(String phoneNumber) {
        if (StringUtils.isNotBlank(phoneNumber)) {
            String temp = CheakTeleUtils.telephoneNumberValid2(DESUtils.decrypt(phoneNumber));
            if (StringUtils.isNotBlank(temp)) {
                this.phoneMobile = "+62" + temp;
            }
        }
    }
}
