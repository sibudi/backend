package com.yqg.common.utils;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.yqg.common.exceptions.BadRequestException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jacob
 *
 */
public class SignUtils {

    private static final Logger logger = LoggerFactory.getLogger(SignUtils.class);

    public static String sign(Long timestamp, String appSecret) {
        StringBuilder temp = new StringBuilder();
        String sign = "";
        try {
            temp.append(
                    toHexValue(encryptMD5(timestamp.toString().getBytes(Charset.forName("utf-8")))))
                    .append(appSecret);
            sign = toHexValue(encryptMD5(temp.toString().getBytes(Charset.forName("utf-8"))));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("md5 error");
        }
        return sign;
    }

    public static boolean signVerify(String sign, Map<String, String> params) {

        String signTarget = sign(params);
        if (signTarget.equals(sign)) {
            return true;
        }
        return false;
    }

    private static String sign(Map<String, String> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        StringBuffer sb = new StringBuffer("");
        sb.append(params.get("sessionId")).append(params.get("timestamp"));
//        for (String s : keys) {
//            sb.append(params.get(s));
//        }
        String sign = "";
        try {
            sign = Base64Utils.encode(SignUtils.generateMd5(sb.toString()).getBytes(Charset.forName("UTF-8")));
            sign = StringUtils.substring(sign, 2, sign.length() - 2);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("md5 error");
        }
        return sign;
    }

    

    public static boolean verifySign(Long timestamp, String appSecret, String sign) {

        String localSign = sign(timestamp, appSecret);
        if (localSign.equals(sign)) {
            return true;
        }
        return false;
    }

    public static String generateMd5(String value) throws BadRequestException {
        return toHexValue(encryptMD5(value.getBytes(Charset.forName("utf-8"))));
    }

    private static byte[] encryptMD5(byte[] data) throws BadRequestException {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            logger.error("{} generate md5 error", data);
            throw new BadRequestException();
        }
        md5.update(data);
        return md5.digest();
    }

    private static String toHexValue(byte[] messageDigest) {
        if (messageDigest == null) {
            return "";
        }
        StringBuilder hexValue = new StringBuilder();
        for (byte aMessageDigest : messageDigest) {
            int val = 0xFF & aMessageDigest;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
}
