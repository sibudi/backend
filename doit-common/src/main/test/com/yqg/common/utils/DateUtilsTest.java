package com.yqg.common.utils;


import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class DateUtilsTest {


    @Test
    public void getBetweenDates() throws ParseException {
        List<Date> betweenDates = DateUtils.getBetweenDates(DateUtils.stringToDate4("2012-01-12"), DateUtils.stringToDate4("2012-01-14"));
        System.out.printf("ss");
    }
}
