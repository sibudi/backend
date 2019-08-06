package com.yqg.drools.beans;

import com.yqg.drools.utils.DateUtil;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/*****
 * @Author zengxiangcai
 * Created at 2018/1/22
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Getter
@Setter
public class ContactData {

    private String createTime;
    private String name;
    private String phone;
    private String note;
    private String updateTime;


    public Date getCreateDate() {
        if (!StringUtils.isNotEmpty(createTime)) {
            return DateUtil.stringToDate(createTime, DateUtil.FMT_YYYY_MM_DD_HH_mm_ss);
        }
        return null;
    }

    public boolean isNotEmpty() {
        if (StringUtils.isNotEmpty(name) && StringUtils.isEmpty(phone)) {
            return true;
        }
        return false;
    }

}
