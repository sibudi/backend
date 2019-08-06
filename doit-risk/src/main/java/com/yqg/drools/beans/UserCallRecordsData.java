package com.yqg.drools.beans;

import com.yqg.common.utils.StringUtils;
import com.yqg.drools.utils.DateUtil;
import java.util.Calendar;
import java.util.Date;
import lombok.Data;
import lombok.Getter;

/**
 * Created by luhong on 2018/1/23.
 */
@Data
public class UserCallRecordsData {

    private String date;//日期
    private String number;//号码
    private String name;//姓名
    private Integer duration;//时长
    private String type;//类型（1=呼入，2=呼出）

    public Date getMsgDate() {
        if (StringUtils.isEmpty(date)) {
            return null;
        }
        return DateUtil.stringToDate(date, DateUtil.FMT_YYYY_MM_DD_HH_mm_ss);
    }

    //(0到5点之间)[大于0点小于5点]
    public boolean isCallInEvening() {
        Date callDate = getMsgDate();
        if (callDate == null) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(callDate);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        if (hour == 0 && minute == 0 && second == 0) {
            return false;
        }
        return hour >= 0 && hour <= 4;
    }

    @Getter
    public enum CallType {
        IN("1"),
        OUT("2");

        CallType(String code) {
            this.code = code;
        }

        private String code;

    }
}
