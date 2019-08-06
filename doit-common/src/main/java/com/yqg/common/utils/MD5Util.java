package com.yqg.common.utils;

import java.security.MessageDigest;

/**
 * md5??
 */
public class MD5Util {
    /**
     * md5??,??????
     * @param string
     * @return
     */
    public static String md5UpCase(String string){
       return md5(string,false);
    }

    /**
     * md5??,??????
     * @param string
     * @return
     */
    public static String md5LowerCase(String string){
        return md5(string,true);
    }

    /**
     * md5??,?????????
     * @param string
     * @param toLowerCase
     * @return
     */
    private static String md5(String string,boolean toLowerCase) {
        // ???????
        char md5String[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            // ???????????? String ??? byte?????????????? byte???
            byte[] btInput = string.getBytes();
            // ??????????????????????????????????????
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // MessageDigest?????? update??????? ?????byte??????
            mdInst.update(btInput);
            // ???????????digest?????????????
            byte[] md = mdInst.digest();
            // ????????????????
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) { // i = 0
                byte byte0 = md[i]; // 95
                str[k++] = md5String[byte0 >>> 4 & 0xf]; // 5
                str[k++] = md5String[byte0 & 0xf]; // F
            }
            // ???????????
            String md5Result=new String(str);
            if(toLowerCase){
                md5Result=md5Result.toLowerCase();
            }
            return md5Result;
        } catch (Exception e) {
            return null;
        }
    }
}
