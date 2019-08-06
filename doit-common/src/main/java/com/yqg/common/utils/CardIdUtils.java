package com.yqg.common.utils;

import java.util.Calendar;

/**
 * 
 * @author Jacob
 *
 */
public class CardIdUtils {

    /**
     * ??????????
     *
     * @param idCard ????
     * @return ??
     */
    public static int getAgeByIdCard(String idCard) {
        int iAge = 0;
        Calendar cal = Calendar.getInstance();
        String year = idCard.substring(10, 12);
        if (Integer.valueOf(year) < 30){
            year = "20"+year;
        }else  {
            year = "19"+year;
        }
        int iCurrYear = cal.get(Calendar.YEAR);
        iAge = iCurrYear - Integer.valueOf(year);
        return iAge;
    }

    /**
     * ??????????
     *
     * @param idCard ????
     * @return ??(yyyyMMdd)
     */
    public static String getBirthByIdCard(String idCard) {
        return idCard.substring(6, 14);
    }

    /**
     * ???????????
     *
     * @param idCard ????
     * @return ??(yyyy)
     */
    public static Short getYearByIdCard(String idCard) {
        return Short.valueOf(idCard.substring(6, 10));
    }

    /**
     * ???????????
     *
     * @param idCard ????
     * @return ??(MM)
     */
    public static Short getMonthByIdCard(String idCard) {
        return Short.valueOf(idCard.substring(10, 12));
    }

    /**
     * ???????????
     *
     * @param idCard ????
     * @return ??(dd)
     */
    public static Short getDateByIdCard(String idCard) {
        return Short.valueOf(idCard.substring(12, 14));
    }



}
