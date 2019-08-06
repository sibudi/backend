package com.yqg.drools.utils;

import java.util.HashMap;

/**
 * Author: tonggen
 * Date: 2019/4/2
 * time: 2:56 PM
 */

public class ModelVariableUtils {

    public static HashMap<String, String> globalVariable = new HashMap<>();

    static {
        globalVariable.put("年龄","RUserInfo($age:age),$age");
        globalVariable.put("性别","RUserInfo($sex:sex),$sex");


        globalVariable.put("信用卡类APP个数","AutoCallModel($appCountForCreditCard:appCountForCreditCard),$appCountForCreditCard");

        globalVariable.put("借款用途","LoanInfo($borrowingPurpose:borrowingPurpose),$borrowingPurpose");
    }
}
