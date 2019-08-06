package com.yqg.common.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ??????
 * Created by Didit Dwianto on 2017/11/26.
 */
public class StringUtils extends org.apache.commons.lang.StringUtils {

    private static String COMMA = ",";
    private static String POINT = ".";

    /**
     * ??????????????????
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }



    /**
     * ?????
     * @param money
     * @return
     */
    public static String formatMoney(Double money) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(money);
    }

    /**
     * ??????????????
     * @param money
     * @return
     */
    public static String convMoney(BigDecimal money) {
        return formatMoney(money.doubleValue()).replaceAll(COMMA,POINT);
    }


    /**
     *  ????????????????
     * @param str1,str2
     * @return
     */
    public static boolean cheakStringContainOtherString(String str1,String str2) {
        if (!StringUtils.isEmpty(str1) && !StringUtils.isEmpty(str2)) {

            if (str1.toUpperCase().contains(str2.toUpperCase())) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    /**
     *  ????
     * @param str1,str2
     * @return
     */
    public static boolean cheakStringEqualsOtherString(String str1,String str2) {
        if (!StringUtils.isEmpty(str1) && !StringUtils.isEmpty(str2)) {

            if (str1.toUpperCase().equals(str2.toUpperCase())) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 李建平
     * 替换NULL并去空值
     * @param one
     * @return
     */
    public static String replaceNull(Object one) {
        if (one==null) {
            return "";
        }
        return one.toString().trim();
    }

}
