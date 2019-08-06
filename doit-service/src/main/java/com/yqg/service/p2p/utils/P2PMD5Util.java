package com.yqg.service.p2p.utils;

import com.alibaba.fastjson.JSON;

import java.security.MessageDigest;

/**
 * md5加密
 */
public class P2PMD5Util {

    private static String salt = "lsjfasflj";

    /**
     * md5加密,字母默认大写
     *
     * @param object
     * @return
     */
    public static String md5UpCase(Object... object) {
        return md5(false, object);
    }

    /**
     * md5加密,字母默认小写
     *
     * @param object
     * @return
     */
    public static String md5LowerCase(Object... object) {
        return md5(true, object);
    }

    /**
     * md5加密,根据参数确定大小写
     *
     * @param object
     * @param toLowerCase
     * @return
     */
    private static String md5(boolean toLowerCase, Object... object) {
        String objectString = JSON.toJSONString(object) + salt;
        // 用于加密的字符
        char md5String[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            // 使用平台的默认字符集将此 String 编码为 byte序列，并将结果存储到一个新的 byte数组中
            byte[] btInput = objectString.getBytes();
            // 信息摘要是安全的单向哈希函数，它接收任意大小的数据，并输出固定长度的哈希值。
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // MessageDigest对象通过使用 update方法处理数据， 使用指定的byte数组更新摘要
            mdInst.update(btInput);
            // 摘要更新之后，通过调用digest（）执行哈希计算，获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) { // i = 0
                byte byte0 = md[i]; // 95
                str[k++] = md5String[byte0 >>> 4 & 0xf]; // 5
                str[k++] = md5String[byte0 & 0xf]; // F
            }
            // 返回经过加密后的字符串
            String md5Result = new String(str);
            if (toLowerCase) {
                md5Result = md5Result.toLowerCase();
            }
            return md5Result;
        } catch (Exception e) {
            return null;
        }
    }
}
