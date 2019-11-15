package com.yqg.common.utils;

/**
 * Created by Jacob on 2017/8/24.
 */

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Map;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.SecretKey;
import javax.crypto.Cipher;

/**
 * DES??
 */
@Component
@Slf4j
public class DESUtils {
    private static String password = "";

    private static String filePath = "";

    @Value("${des.file.path}")
    public void setFilePaht(String path) {
        filePath = path;
    }

    //??
    public static void main(String args[]) throws Exception{
        String mobile = "87782407500";
        String password = "A5880DD820109132570743325311898426347857298773549468758875018579537757772163084478873699447306034466200AAA411960574122434059469SSS235892702736860872901247123456";
//        byte[] encrypt = encrypt(mobile.getBytes(), password);
//        String result = Base64.encodeBase64String(encrypt);
//        System.err.println(result)

        byte[] encrypt = new byte[0];
        try {
            encrypt = decrypt(Base64.decodeBase64("y1IKt9TA2xpstz4rSWStiw=="), password);
        } catch (Exception e) {
            log.error("decrypt error, data=" , e);
        }

        System.err.println(new String(encrypt));
    }



    private static String getPassword(){
        if(StringUtils.isEmpty(password)){
            password=TextToFieldUtils.readFileByLines(filePath);
        }
        return password;
//        return "A5880DD820109132570743325311898426347857298773549468758875018579537757772163084478873699447306034466200AAA411960574122434059469SSS235892702736860872901247123456";
    }

    /**
     * ?????
     * @param data
     * @return
     */
    public static String encrypt(String data) {
        byte[] encrypt = encrypt(data.getBytes(), getPassword());
        String result = Base64.encodeBase64String(encrypt);
        return result;
    }

    public static String encryptNew(String data) {
        byte[] encrypt = encrypt(data.getBytes(), getPassword());
        String result = Base64.encodeBase64String(encrypt);
        return result;
    }
    /**
     * ?????
     * @param data
     * @return
     */
    public static String decrypt(String data) {

        byte[] encrypt = new byte[0];
        try {
            encrypt = decrypt(Base64.decodeBase64(data), getPassword());
        } catch (Exception e) {
            log.error("decrypt error, data=" + data, e);
        }
        String result = new String(encrypt);
        return result;
    }

    public static String decryptNew(String data) {
        byte[] encrypt = new byte[0];
        try {
            encrypt = decrypt(Base64.decodeBase64(data), getPassword());
        } catch (Exception e) {
            log.error("decrypt error, data=" + data, e);
        }
        String result = new String(encrypt);
        return result;
    }
    /**
     * ??
     *
     * @param datasource byte[]
     * @param password   String
     * @return byte[]
     */
    public static byte[] encrypt(byte[] datasource, String password) {
        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(password.getBytes());
            //??????????????DESKeySpec???
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            //Cipher??????????
            Cipher cipher = Cipher.getInstance("DES");
            //??????Cipher??
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            //??????????
            //????????
            return cipher.doFinal(datasource);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ??
     *
     * @param src      byte[]
     * @param password String
     * @return byte[]
     * @throws Exception
     */
    private static byte[] decrypt(byte[] src, String password) throws Exception {
        // DES???????????????
        SecureRandom random = new SecureRandom();
        // ????DESKeySpec??
        DESKeySpec desKey = new DESKeySpec(password.getBytes());
        // ????????
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        // ?DESKeySpec?????SecretKey??
        SecretKey securekey = keyFactory.generateSecret(desKey);
        // Cipher??????????
        Cipher cipher = Cipher.getInstance("DES");
        // ??????Cipher??
        cipher.init(Cipher.DECRYPT_MODE, securekey, random);
        // ????????
        return cipher.doFinal(src);
    }
}

