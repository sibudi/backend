package com.yqg.common.utils;

import com.sun.org.apache.regexp.internal.RE;
import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/5
 * @Email zengxiangcai@yishufu.com
 * AES 加解密
 ****/

@Slf4j
public class AesUtil {

    public static final String AES_CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding";
    public static final String ENCODING_UTF8 = "UTF-8";
    public static final String ALGORITHM_AES = "AES";

    public static final String PRIVATE_KEY = "97dgasw1loiasw8l";
    public static final String IV = "qwe512rty123nji7";

    public static String encryptData(String plainData, String privateKey, String initVector) {
        try {
            log.info("aes加密入参: "+plainData);
            byte rawKey[] = privateKey.getBytes();
            SecretKeySpec keySpec = new SecretKeySpec(rawKey, ALGORITHM_AES);

            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes());

            Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

            byte encryptedData[] = cipher.doFinal(plainData.getBytes(ENCODING_UTF8));

            String result = new String(Base64.getEncoder().encode(encryptedData));
           log.info("aes加密结果："+result);
            return result;
        } catch (Exception e) {
            log.error("encrypted error,plainData: "+plainData,e);
            return null;
        }
    }

    public static String decryptData(String cipherText, String privateKey, String initVector) {
        try {
            byte rawKey[] = privateKey.getBytes();
            SecretKeySpec keySpec = new SecretKeySpec(rawKey, ALGORITHM_AES);

            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes());

            Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);

            byte decryptText[] = new BASE64Decoder().decodeBuffer(cipherText);
            byte originText[] = cipher.doFinal(decryptText);
            String result = new String(originText, ENCODING_UTF8);
            return result;
        } catch (Exception e) {
            log.error("decrypt error,cipherText: "+cipherText,e);
            return null;
        }
    }


    public static String encryptMobile(String plainData) {
        return encryptData(plainData,PRIVATE_KEY,IV);
    }


    public static String decryptMobile(String cipherText) {
        return decryptData(cipherText,PRIVATE_KEY,IV);
    }
}
