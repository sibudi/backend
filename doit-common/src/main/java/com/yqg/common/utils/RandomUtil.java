package com.yqg.common.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ??????
 * Created by Jacob on 2017/9/3.
 */
public class RandomUtil {

    /**
     * ??????
     * @param rateString ?????,??????=10 ???5:3:2?
     * @return
     */
    public static int getRandom(String rateString){
        List<Double> rateList=new ArrayList<>();
        String[] rates=rateString.split(":");
        for (String rate : rates) {
            rateList.add(Integer.parseInt(rate)/10.0);
        }
        return getRandom(rateList);
    }

    /**
     * ????,????
     * @param orignalRates
     * @return
     */
    public static int getRandom(List<Double> orignalRates) {
        if (orignalRates == null || orignalRates.isEmpty()) {
            return -1;
        }
        int size = orignalRates.size();
        // ???????????????????1
        double sumRate = 0d;
        for (double rate : orignalRates) {
            sumRate += rate;
        }
        // ???????????????????
        List<Double> sortOrignalRates = new ArrayList<Double>(size);
        Double tempSumRate = 0d;
        for (double rate : orignalRates) {
            tempSumRate += rate;
            sortOrignalRates.add(tempSumRate / sumRate);
        }
        // ????????????????
        double nextDouble = Math.random();
        sortOrignalRates.add(nextDouble);
        Collections.sort(sortOrignalRates);

        return sortOrignalRates.indexOf(nextDouble);
    }
}
