package com.yqg.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jacob on 2017/9/1.
 */
public class WeekUtil {



    public static void main(String[] args) {
        int weekNumber=WeekUtil.getDayofweek(DateUtils.formDate(new Date(),"yyyy-MM-dd"));
        System.out.println(weekNumber);
        if(weekNumber!=0&&weekNumber!=1){
            System.out.println("aaa");
        }
    }

    public static int getDayofweek(String date) {
        Calendar cal = Calendar.getInstance();
        if (date.equals("")) {
            cal.setTime(new Date(System.currentTimeMillis()));
        } else {
            cal.setTime(new Date(getDateByStr2(date).getTime()));
        }
        return cal.get(Calendar.DAY_OF_WEEK)-1;
    }


    public static Date getDateByStr2(String dd) {

        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = sd.parse(dd);
        } catch (ParseException e) {
            date = null;
            e.printStackTrace();
        }
        return date;
    }

}
