package com.yqg.common.utils;

/**
 *
 * Created by Didit Dwianto on 2017/12/29.
 */
public class NumberUtils {

    /**
     * ??
     * @param time1
     * @param time2
     * @return
     */
    public static Float division(Object time1, Object time2) {
        if(Float.parseFloat(time2.toString()) == 0f){
            return 0f;
        }else {
            Float num =  Float.parseFloat(time1.toString()) / Float.parseFloat(time2.toString());
            return num;
        }
    }





}
