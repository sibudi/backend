package com.yqg.common.utils;


import org.apache.commons.lang3.StringUtils;

/**
 * ???????
 * @author alan
 */
public class SensitiveInfoUtils {

    /**
     * [????] ??????????????2???<????**>
     *
     * @param fullName
     * @return
     */
    public static String chineseName(String fullName) {
        if(StringUtils.isEmpty(fullName)){
            return "";
        }
        String name = StringUtils.left(fullName,1);
        return StringUtils.rightPad(name,StringUtils.length(fullName),"*");
    }

    /**
     * [????] ??????????????18???15??<???*************5762>
     *
     * @param idCard
     * @return
     */
    public static String idCardNum(String idCard) {
        if(StringUtils.isEmpty(idCard)){
            return "";
        }

        String num = StringUtils.right(idCard,4);
        return StringUtils.leftPad(num,StringUtils.length(idCard),"*");
    }

    /**
     * [????] ????????????<??:138******1234>
     *
     * @param num
     * @return
     */
    public static String mobilePhone(String num) {
        if (StringUtils.isBlank(num)) {
            return "";
        }
        return StringUtils.left(num, 3).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(num, 4), StringUtils.length(num), "*"), "***"));
    }

    /**
     * [????] ?????????????????1???<??:6222600**********1234>
     *
     * @param cardNum
     * @return
     */
    public static String bankCard(String cardNum) {
        if (StringUtils.isBlank(cardNum)) {
            return "";
        }
        return StringUtils.left(cardNum, 6).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(cardNum, 4), StringUtils.length(cardNum), "*"), "******"));
    }


}
