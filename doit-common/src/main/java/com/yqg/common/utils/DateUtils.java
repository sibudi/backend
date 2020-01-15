/*
 * Copyright (c) 2014-2015 XXX, Inc. All Rights Reserved.
 */

package com.yqg.common.utils;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Utility functions for date related logic.
 *
 * @author Jacob
 */
@Slf4j
public class DateUtils {

    public static final String FMT_YYYY_MM_DD = "yyyy-MM-dd";

    public static final String FMT_YYYYMMDD = "yyyyMMdd";

    public static final String FMT_DDMMYYYY = "dd/MM/yyyy";

    public static final String FMT_YYYY_MM_DD_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";

    /**
     * ?????? ?? ??num???????
     *
     * @param endDay
     * @param num
     * @return
     */
    public static Date getDiffTime(Date endDay, Integer num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDay);
        calendar.add(Calendar.DATE, num);
        return calendar.getTime();
    }

    /**
     * Convert Long to String of Day for targeted time zone.
     *
     * @param epochTime
     * @param
     * @return
     */
    public static String epochTimeToDay(long epochTime) {
        Date date = new Date(epochTime);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }

    /**
     * Convert Long to String of Day for targeted time zone.
     *
     * @param epochTime second
     * @param timeZone
     * @return
     */
    public static String epochSecondToDay(long epochTime, String timeZone) {
        Date date = new Date(epochTime * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        return simpleDateFormat.format(date);
    }

    /**
     * Convert current Date to String of Day
     *
     * @return
     */
    public static String dateToDay() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }

    /**
     * Convert String of day to epoch time for targeted time zone.
     *
     * @param day
     * @param timeZone
     * @return
     * @throws ParseException
     */
    public static long dayToMilliSeconds(String day, String timeZone) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        return simpleDateFormat.parse(day).getTime();
    }

    /**
     * Convert String of day to epoch time for targeted time zone.
     *
     * @param day
     * @param
     * @return
     * @throws ParseException
     */
    public static long timetoMilliSeconds(String day) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return simpleDateFormat.parse(day).getTime();
    }

    /**
     * Convert String of day to epoch time for targeted time zone.
     *
     * @param day
     * @param timeZone
     * @return
     * @throws ParseException
     */
    public static long dayToSeconds(String day, String timeZone) throws ParseException {
        return dayToMilliSeconds(day, timeZone) / 1000;
    }

    /**
     * Convert String of day to epoch time for UTC
     *
     * @param day
     * @return
     * @throws ParseException
     */
    public static long dayToUTCSeconds(String day) throws ParseException {
        return dayToSeconds(day, "UTC");
    }

    /**
     * Convert String of day to Date type
     *
     * @param time
     * @param format
     * @return
     * @throws ParseException
     */
    public static Date strToDate(String time, String format) throws ParseException {
        DateFormat formater = new SimpleDateFormat(format);
        return formater.parse(time);
    }

    /**
     * Get day of week
     *
     * @param date
     * @return
     */
    public static int getDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * date to string yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static String DateToString(Date date) {
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
        return sFormat.format(date);
    }

    /**
     * date to string yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String DateToString2(Date date) {
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sFormat.format(date);
    }

    public static String DateToString3(Date date) {
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return sFormat.format(date);
    }

    public static String DateToString4(Date date) {
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyyMMdd");
        return sFormat.format(date);
    }

    public static String DateToString5(Date date) {
        SimpleDateFormat sFormat = new SimpleDateFormat("dd-MM-yyyy");
        return sFormat.format(date);
    }

    public static String formDate(Date date, String dateStr) {
        SimpleDateFormat sFormat = new SimpleDateFormat(dateStr);
        return sFormat.format(date);
    }

    public static String formatDateWithLocale(Date date, String dateStr, Locale locale) {
        SimpleDateFormat sFormat = new SimpleDateFormat(dateStr,locale);
        return sFormat.format(date);
    }

    public static String DateToString6(Date date) {
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy.MM.dd");
        return sFormat.format(date);
    }
    /**
     * ??????
     *
     * @param
     * @param
     */
    public static boolean compareDateTime(Date currentDate, Date endDate) {
        if (currentDate.getTime() > endDate.getTime()) {
            return true;
        } else {
            return false;
        }
    }

    public static String decrMonthToString(int count) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -count);
        Date m = c.getTime();
        String mon = format.format(m);
        return mon;
    }

    public static String decrDayToString(int count) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, -count);
        Date d = c.getTime();
        String day = format.format(d);
        return day;
    }

    public static Date stringToDate(String time) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = format.parse(time);
        return date;
    }


    public static Date stringToDate2(String time) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = format.parse(time);
        return date;
    }

    /**
     * 获取今天的开始时间
     *
     * @param time
     * @return
     * @throws ParseException
     */
    public static Date getTodayStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取今天零点整的时间
     */
    public static Date getTodayEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * ??????
     *
     * @param currentDate
     * @param setDate
     * @return
     */
    public static long getDateTimeDifference(Date currentDate, Date setDate) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long nd = 1000 * 24 * 60 * 60;//??????
        long nh = 1000 * 60 * 60;//???????
        //        long nm = 1000*60;//???????
        //        long ns = 1000;//???????long diff;
        //?????????????
        long diff = 0;
        try {
            diff = sd.parse(sd.format(currentDate)).getTime() - sd.parse(sd.format(setDate)).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long day = diff / nd;//??????
        long hour = diff % nd / nh;//???????
        //        long min = diff%nd%nh/nm;//???????
        //        long sec = diff%nd%nh%nm/ns;//??????//????
        return day;
    }

     /**
     * 通过时间秒毫秒数判断两个时间的间隔
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDaysByMillisecond(Date date1,Date date2)
    {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
        return days;
    }


    /**
     * ??????
     *
     * @param currentDate
     * @param setDate
     * @return
     */
    public static long getDateTimeDifference2(Date currentDate, Date setDate) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long nd = 1000 * 24 * 60 * 60;//??????
        long nh = 1000 * 60 * 60;//???????
        long nm = 1000 * 60;//???????
        //        long ns = 1000;//???????long diff;
        //?????????????
        long diff = 0;
        try {
            diff = sd.parse(sd.format(currentDate)).getTime() - sd.parse(sd.format(setDate)).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long min = diff % nd % nh / nm;//???????
        return min;
    }

    /**
     * ??+n?
     *
     * @param date
     * @return date
     */
    public static Date addDate(Date date, int day) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, day);//?????????.?????,??????
        date = calendar.getTime();   //????????????????
        return date;
    }

    /**
     * ??+n??
     *
     * @param date
     * @return date
     */
    public static Date addDateWithHour(Date date, int hour) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.HOUR, hour);//?????????.?????,??????
        date = calendar.getTime();   //????????????????
        return date;
    }

    /**
     * 增加月份
     *
     * @param date
     * @return date
     */
    public static Date addDateWithMonth(Date date, int mouth) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.MONTH, mouth);
        date = calendar.getTime();
        return date;
    }


    /**
     * ??????
     *
     * @param smdate
     * @param bdate
     * @return
     * @throws ParseException
     */
    public static int daysBetween(String smdate, String bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(between_days));
    }


    /**
     * ????
     *
     * @param begin
     * @param end
     * @return
     */
    public static long getDiffDaysIgnoreHours(Date begin, Date end) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date beginDate = simpleDateFormat.parse(simpleDateFormat.format(begin));
            Date endDate = simpleDateFormat.parse(simpleDateFormat.format(end));
            return (endDate.getTime() - beginDate.getTime()) / (1000 * 3600 * 24);
        } catch (Exception e) {
            log.error("parse date error", e);
        }
        return 0;
    }

    public static int compare_date(Date date1, Date date2) {
        int i = date1.compareTo(date2);
        return i;

    }

    public static Date stringToDate4(String time) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = format.parse(time);
        return date;
    }

    public static Date stringToDate5(String time) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date date = format.parse(time);
        return date;
    }

    public static Date stringToDate6(String time) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date date = format.parse(time);
        return date;
    }

    public static Date stringToDate(String time, String strFormat) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(strFormat);
            Date date = format.parse(time);
            return date;
        } catch (Exception e) {
            log.error("parse date error: " + time + ", format: " + strFormat, e);
        }
        return null;
    }


    /***
     * 增加分钟
     * @param date
     * @param minutes
     * @return
     */
    public static Date addDateWithMinutes(Date date, int minutes) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.MINUTE, minutes);
        date = calendar.getTime();
        return date;
    }

    public static Date dateFormat(Date date, String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        String strDate = format.format(date);
        try {
            return format.parse(strDate);
        } catch (ParseException e) {
            log.info("date parse error", e);
        }
        return null;
    }

    /**
     * 获取两个日期之间的日期
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 日期集合
     */
    public static List<Date> getBetweenDates(Date start, Date end) {
        List<Date> result = new ArrayList<Date>();
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(start);
        tempStart.add(Calendar.DAY_OF_YEAR, 1);
        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTime(end);
        while (tempStart.before(tempEnd)) {
            result.add(tempStart.getTime());
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
        }
        return result;
    }

    public static List<Date> getBetweenDatesAndStartEnd(Date start, Date end) {
        List<Date> betweenDates = getBetweenDates(start, end);
        betweenDates.add(start);
        // 开始和结束不是同一天
        if (!DateToString4(start).equalsIgnoreCase(DateToString4(end))){
            betweenDates.add(end);
        }
        return betweenDates;
    }

    public static Date getRefundDate(Integer productType, Integer term, Date nowDate){
        if(productType >= 300){
            //yearly
            return DateUtils.addDate(DateUtils.addDate(nowDate, term * (productType - 300) * 365),-1);
        }
        else if(productType >= 200){
            //weekly
            return DateUtils.addDate(DateUtils.addDate(nowDate,term * (productType - 200) * 7),-1);
        }
        else if(productType >= 100){
            //monthly
            return DateUtils.addDate(DateUtils.addDateWithMonth(nowDate,term * (productType - 100)),-1);
        }
        else if(productType == 1){
            //monthly
            return DateUtils.addDate(DateUtils.addDateWithMonth(nowDate,term),-1);
        }
        else{
            //daily
            return DateUtils.addDate(DateUtils.addDate(nowDate,term),-1);
        }
    }

}
