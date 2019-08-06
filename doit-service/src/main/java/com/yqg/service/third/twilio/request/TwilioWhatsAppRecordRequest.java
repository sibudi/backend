package com.yqg.service.third.twilio.request;

import com.yqg.system.entity.TwilioWhatsAppRecord;
import lombok.Data;

/**
 * Author: tonggen
 * Date: 2018/10/16
 * time: 下午2:09
 */
@Data
public class TwilioWhatsAppRecordRequest extends TwilioWhatsAppRecord{

    //查询逾期几天的订单
    private Integer days;
}
