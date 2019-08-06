package com.yqg.drools.beans;

import com.yqg.common.utils.StringUtils;
import com.yqg.drools.utils.DateUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/*****
 * @Author zengxiangcai
 * Created at 2018/1/23
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Getter
@Setter
public class InstalledAppData {
    private String appName;
    private String appVersion;
    private String appBundleId;

    private String appInstallTime;
    private String applastUpdateTime;

    public Date getInstalledDate(){
        if(StringUtils.isEmpty(appInstallTime)){
            return null;
        }
        return DateUtil.stringToDate(appInstallTime,DateUtil.FMT_YYYY_MM_DD_HH_mm_ss);
    }

    public Date getLastUpdateTime(){
        if(StringUtils.isEmpty(applastUpdateTime)){
            return null;
        }
        return DateUtil.stringToDate(applastUpdateTime,DateUtil.FMT_YYYY_MM_DD_HH_mm_ss);
    }
}
