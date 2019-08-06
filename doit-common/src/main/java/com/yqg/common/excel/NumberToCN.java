package com.yqg.common.excel;

/**
 * ???????
 * Created by Jacob on 2017/4/19.
 */
import java.math.BigDecimal;

/**
 * ??????????????<br>
 *
 * @author hongten
 * @contact hongtenzone@foxmail.com
 * @create 2013-08-13
 */
public class NumberToCN {
    /**
     * ???????
     */
    private static final String[] CN_UPPER_NUMBER = { "?", "?", "?", "?", "?",
            "?", "?", "?", "?", "?" };
    /**
     * ?????????????????????
     */
    private static final String[] CN_UPPER_MONETRAY_UNIT = { "?", "?", "?",
            "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?",
            "?", "?" };
    /**
     * ??????
     */
    private static final String CN_FULL = "???";
    /**
     * ??????
     */
    private static final String CN_NEGATIVE = "?";
    /**
     * ??????????2
     */
    private static final int MONEY_PRECISION = 2;
    /**
     * ????????
     */
    private static final String CN_ZEOR_FULL = "??" + CN_FULL;

    /**
     * ??????????????????
     *
     * @param numberOfMoney
     *            ?????
     * @return ???????
     */
    public static String number2CNMontrayUnit(BigDecimal numberOfMoney) {
        StringBuffer sb = new StringBuffer();
        // -1, 0, or 1 as the value of this BigDecimal is negative, zero, or
        // positive.
        int signum = numberOfMoney.signum();
        // ??????
        if (signum == 0) {
            return CN_ZEOR_FULL;
        }
        //????????????
        long number = numberOfMoney.movePointRight(MONEY_PRECISION)
                .setScale(0, 4).abs().longValue();
        // ?????????
        long scale = number % 100;
        int numUnit = 0;
        int numIndex = 0;
        boolean getZero = false;
        // ????????????????00 = 0, 01 = 1, 10, 11
        if (!(scale > 0)) {
            numIndex = 2;
            number = number / 100;
            getZero = true;
        }
        if ((scale > 0) && (!(scale % 10 > 0))) {
            numIndex = 1;
            number = number / 10;
            getZero = true;
        }
        int zeroSize = 0;
        while (true) {
            if (number <= 0) {
                break;
            }
            // ??????????
            numUnit = (int) (number % 10);
            if (numUnit > 0) {
                if ((numIndex == 9) && (zeroSize >= 3)) {
                    sb.insert(0, CN_UPPER_MONETRAY_UNIT[6]);
                }
                if ((numIndex == 13) && (zeroSize >= 3)) {
                    sb.insert(0, CN_UPPER_MONETRAY_UNIT[10]);
                }
                sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
                sb.insert(0, CN_UPPER_NUMBER[numUnit]);
                getZero = false;
                zeroSize = 0;
            } else {
                ++zeroSize;
                if (!(getZero)) {
                    sb.insert(0, CN_UPPER_NUMBER[numUnit]);
                }
                if (numIndex == 2) {
                    if (number > 0) {
                        sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
                    }
                } else if (((numIndex - 2) % 4 == 0) && (number % 1000 > 0)) {
                    sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
                }
                getZero = true;
            }
            // ?number??????????
            number = number / 10;
            ++numIndex;
        }
        // ??signum == -1??????????????????????????
        if (signum == -1) {
            sb.insert(0, CN_NEGATIVE);
        }
        // ????????????"00"?????????????????
        if (!(scale > 0)) {
            sb.append(CN_FULL);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        double money = 2020004.01;
        BigDecimal numberOfMoney = new BigDecimal(money);
        String s = NumberToCN.number2CNMontrayUnit(numberOfMoney);
        System.out.println("?????????"+ money +"?   #--# [" +s.toString()+"]");
    }
}
