package com.yqg.manage.util;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: tonggen
 * Date: 2018/6/20
 * time: 下午5:02
 */
public class NoSecondCheckUtils {

    public static Map<String, Boolean> getAllType() {

        Map<String, Boolean> result = new HashMap<>();

        result.put("COMB_2_FEMALE_FREE_PHONE_CHECK", true);
        result.put("COMB_3_SEX_PHONE_LANGUAGE", true);
        result.put("COMB_RECENT30_MISCALL_FEMALE_MARRIAGE_EDUCATION", true);
        result.put("COMB_AGE_FEMALE_EDUCATION", true);
        result.put("COMB_FIRST_CONTACT_RECENT90CALL_FEMALE", true);
        result.put("COMB_RECENT180RIDETIMES_FEMALE", true);
        result.put("COMB_DIFFDAYSOFEARLIESTMSG_RECENT90OV", true);
        result.put("EDUESMSG_FEMALE", true);
        result.put("FACEBOOK_COMPANY_NOT_CONTAIN", false);

        return result;
    }

//    public static String getCodeStrs(Map<String, Boolean> result) {
//
//        String
//        for (String str : result.keySet()) {
//
//        }
//    }
}
