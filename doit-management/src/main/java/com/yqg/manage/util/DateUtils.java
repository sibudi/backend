package com.yqg.manage.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author Jacob
 */

@Slf4j
public class DateUtils {

    private final  static long MILLION_SECOND_PER_DAY = 1000L * 24 * 60 * 60;
    public static final String FMT_YYYY_MM_DD = "yyyy-MM-dd";
    public static final long MILLION_SECOND_PER_YEAR = 365L * 1000 * 24 * 60 * 60;

    /**
     * ??????????
     * @param lastDate
     * @param remoteDate
     * @return
     */
    public static long getYearInterval(String lastDate, String remoteDate) {

        if (StringUtils.isEmpty(lastDate) || StringUtils.isEmpty(remoteDate)) {
            return -1;
        }

        SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
        //??????
        long yearDn = MILLION_SECOND_PER_YEAR;
        //?????????????
        long diff = 0;
        try {
            diff = sf.parse(lastDate).getTime()
                    - sf.parse(remoteDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return diff / yearDn;

    }

    /**
     * ??????????
     * @param lastDate
     * @param remoteDate
     * @return
     */
    public static long getDateInterval(String lastDate, String remoteDate) {

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        //?????????????
        long diff = 0;
        try {
            diff = sf.parse(lastDate).getTime()
                    - sf.parse(remoteDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return diff / MILLION_SECOND_PER_DAY;

    }

    /**
     * ??????????
     *
     * @param past
     * @return
     */
    public static String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
        return result;
    }

    /**
     * ???? ? past ????
     * @param past
     * @return
     */
    public static String getFetureDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat(FMT_YYYY_MM_DD);
        String result = format.format(today);
        return result;
    }

    /**
     * ?????????*/
    public static String getCurrentTimeToHour() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
        return simpleDateFormat.format(date);
    }

    /**
     * ?????????*/
    public static String getCurrentTimeToMinute() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return simpleDateFormat.format(date);
    }

    /**
     * ????????*/
    public static int getCurrentTimeMinute() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MINUTE);
    }

    /***
     * ?????????????????
     * @param begin
     * @param end
     * @return
     */
    public static long getDiffDaysIgnoreHours(Date begin, Date end) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FMT_YYYY_MM_DD);
        try {
            Date beginDate = simpleDateFormat.parse(simpleDateFormat.format(begin));
            Date endDate = simpleDateFormat.parse(simpleDateFormat.format(end));
            return (endDate.getTime() - beginDate.getTime()) / MILLION_SECOND_PER_DAY;
        } catch (Exception e) {
            log.error("parse date error", e);
        }
        return 0;
    }

    public static String getStrCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FMT_YYYY_MM_DD);
        return simpleDateFormat.format(new Date());
    }
}
