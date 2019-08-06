package com.yqg.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * ????
 * @author  Jacob
 */
public class OrderNoCreator {
    private static Logger logger = LoggerFactory.getLogger(OrderNoCreator.class);


    public static void main(String[] args) {
        System.out.println(createOrderNo());
    }



    private static final String machine ="01";   // 普通订单
    private static final String machine2 ="02";  // 好友活动订单
    private static final String machine3 ="03";  // 账单

    //?????????
    public static String createOrderNo() {
        String dateFormat = DateUtils.formDate(new Date(), "yyMMdd");
        String timeFormat = DateUtils.formDate(new Date(), "HHmmss");
        StringBuilder builder = new StringBuilder(machine);
        builder.append(dateFormat).append(timeFormat).append(IDCreator.create());
        return builder.toString();
    }

    public static String createOrderNo2(){
        String dateFormat = DateUtils.formDate(new Date(), "yyMMdd");
        String timeFormat = DateUtils.formDate(new Date(), "HHmmss");
        StringBuilder builder = new StringBuilder(machine2);
        builder.append(dateFormat).append(timeFormat).append(IDCreator.create());
        return builder.toString();
    }

    public static String createOrderNo3(){
        String dateFormat = DateUtils.formDate(new Date(), "yyMMdd");
        String timeFormat = DateUtils.formDate(new Date(), "HHmmss");
        StringBuilder builder = new StringBuilder(machine3);
        builder.append(dateFormat).append(timeFormat).append(IDCreator.create());
        return builder.toString();
    }
}

class IDCreator {
    private static Long second = 0L;
    private static Integer seed = 0;

    private synchronized static String getId() {
        if (second == 0)
            second = System.currentTimeMillis();
        if (second != System.currentTimeMillis()) {
            second = System.currentTimeMillis();
            seed = 0;
            return second.toString() + seed;
        } else {
            return second.toString() + ++seed;
        }
    }

    public static String create() {
        String id = getId();
        return id.substring(id.length() - 4, id.length());
    }
}