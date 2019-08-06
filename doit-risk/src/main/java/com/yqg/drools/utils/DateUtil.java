package com.yqg.drools.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Slf4j
public class DateUtil {

    public static final String FMT_YYYY_MM_DD = "yyyy-MM-dd";

    public static final String FMT_YYYYMMDD = "yyyyMMdd";

    public static final String FMT_DDMMYYYY = "dd/MM/yyyy";

    public static final String FMT_MMYYYY = "MM/yyyy";


    public static final String FMT_YYYY_MM_DD_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";

    public static final Long MILLION_SECONDS_PER_DAY = 86400000L;

    public static final Long MILLION_SECONDS_PER_MINUTE = 60000L;



    public static final Long DAYS_PER_MONTH = 30L;

    public static boolean filterLimitDate(Date limitStartDate, Date compareDate,
        Date limitEndDate) {
        if (limitStartDate == null || compareDate == null || limitEndDate == null) {
            return false;
        }

        if (compareDate != null) {
            return compareDate.after(limitStartDate) && limitEndDate
                .after(compareDate);//  limitStartDate < compareDate < limitEndDate
        }

        return true;
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

    public static long getDiffDaysIgnoreHours(Date begin, Date end) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FMT_YYYY_MM_DD);

        try {
            Date beginDate = simpleDateFormat.parse(simpleDateFormat.format(begin));
            Date endDate = simpleDateFormat.parse(simpleDateFormat.format(end));
            return (endDate.getTime() - beginDate.getTime()) / MILLION_SECONDS_PER_DAY;
        } catch (Exception var5) {
            log.error("parse date error", var5);
            return 0L;
        }
    }

    public static long getDiffMinutes(Date begin, Date end) {
        return (end.getTime() - begin.getTime()) / MILLION_SECONDS_PER_MINUTE;
    }

    public static long getDiffDays(Date begin, Date end) {
        return (end.getTime() - begin.getTime()) / MILLION_SECONDS_PER_DAY;
    }

    public static Date addMonth(Date d, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.add(Calendar.MONTH, month);
        return calendar.getTime();

    }

    public static Date formatDate(Date d, String format) {
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        try {
            return fmt.parse(fmt.format(d));
        } catch (ParseException e) {
            log.error("parse date error, date : " + d + " , fmt: " + format, e);
        }
        return null;
    }


    public static int getDiffMonthsIgnoreDays(Date start, Date end) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(start);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(end);

        int diffYears = (calendar2.get(Calendar.YEAR) - calendar1.get(Calendar.YEAR));
        int diffMonth = (calendar2.get(Calendar.MONTH) - calendar1.get(Calendar.MONTH));
        return diffYears * 12 + diffMonth;
    }


    public static void main(String[] args) {
        Date d1= DateUtil.stringToDate("01/01/2018",DateUtil.FMT_DDMMYYYY);
        Date d2= DateUtil.stringToDate("30/11/2016",DateUtil.FMT_DDMMYYYY);
        System.err.println(getDiffMonthsIgnoreDays(d2,d1));
    }
}
