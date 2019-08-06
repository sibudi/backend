package com.yqg.drools.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.utils.StringUtils;
import com.yqg.drools.utils.DateUtil;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author zengxiangcai
 * Created at 2018/1/23
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Getter
@Setter
public class ShortMessageData {

    private String date;
    private String phoneNumber;
    @JsonProperty(value = "smsbody")
    private String smsBody;
    private String type;

    public Date getMsgDate() {
        if (StringUtils.isEmpty(date)) {
            return null;
        }
        return DateUtil.stringToDate(date, DateUtil.FMT_YYYY_MM_DD_HH_mm_ss);
    }
}
