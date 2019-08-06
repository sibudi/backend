package com.yqg.common.utils;

import org.springframework.http.converter.HttpMessageNotReadableException;

import java.io.IOException;

/**
 * Created by Jacob on 2017/6/17.
 */
public class RedUtil {


    public static String reads(String str)
            throws IOException, HttpMessageNotReadableException {

        String json = null;
        try {
            byte[] data = Base64Utils.decode(str);
            byte[] decodedData = RSAUtils.decryptByPrivateKey(data,
                    RSAUtils.privateKeyStr);
            json = new String(decodedData);
        } catch (Exception e) {
        }
        return json;
    }
}
