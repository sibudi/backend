package com.yqg.service.third.ojk.aes;

/**
 * Created by wanghuaizhou on 2019/6/12.
 */

import com.yqg.common.utils.Base64Utils;
import com.yqg.service.third.ojk.config.OJKConfig;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.util.Base64;

@Slf4j
public class AESUtils {

    private static final String AES = "AES";
    private static final String AES_CBC = "AES/CBC/PKCS5Padding";
    private static final char[] DIGITS_LOWER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 加密
     *
     * @param data 待加密明文数据
     * @param key  加密密码
     * @param iv   加密向量（偏移量）
     * @return
     */
    public static byte[] encrypt(byte[] data, byte[] key, byte[] iv) {
        return aes(data, key, iv, Cipher.ENCRYPT_MODE);
    }

    /**
     * 解密
     *
     * @param encryptedData 加密后的数据
     * @param key           解密密码
     * @param iv            解密向量（偏移量）
     * @return
     */
    public static byte[] decrypt(byte[] encryptedData, byte[] key, byte[] iv) {
        return aes(encryptedData, key, iv, Cipher.DECRYPT_MODE);
    }

    /**
     * 使用AES加密或解密无编码的原始字节数组, 返回无编码的字节数组结果.
     *
     * @param input 原始字节数组
     * @param key   符合AES要求的密钥
     * @param iv    初始向量
     * @param mode  Cipher.ENCRYPT_MODE 或 Cipher.DECRYPT_MODE
     */
    private static byte[] aes(byte[] input, byte[] key, byte[] iv, int mode) {
        try {
            SecretKey secretKey = new SecretKeySpec(key, AES);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(AES_CBC);
            cipher.init(mode, secretKey, ivSpec);
            return cipher.doFinal(input);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] decodeHex(final char[] data) throws Exception {

        final int len = data.length;

        if ((len & 0x01) != 0) {
            throw new Exception("Odd number of characters.");
        }

        final byte[] out = new byte[len >> 1];

        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }

        return out;
    }

    /**
     * Hex解码.
     */
    public static byte[] decodeHex(String input) {
        try {
            return decodeHex(input.toCharArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int toDigit(final char ch, final int index) throws Exception {
        final int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new Exception("Illegal hexadecimal character " + ch + " at index " + index);
        }
        return digit;
    }

    public static char[] encodeHex(final byte[] data, final char[] toDigits) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }



    public static String encodeHexString(final byte[] data) {
        return new String(encodeHex(data));
    }

    public static char[] encodeHex(final byte[] data){
        return encodeHex(data, DIGITS_LOWER);
    }


//    public static void main(String[] args) throws Exception {
//
//        //密码：4bd393e7a457f9023d9ba95fffb5a2e11a198decb8678bdb37ff0113a2c3e3d2
//        //向量（偏移量）：0c6607d493c434bfd315c84411ae4c89
//        String password = "4bd393e7a457f9023d9ba95fffb5a2e11a198decb8678bdb37ff0113a2c3e3d2";
//        String iv = "0c6607d493c434bfd315c84411ae4c89";
//
//        byte[] passwordBinaryArray = decodeHex(password); //passwordBinaryArray 长度为16个字节
//        byte[] ivBinaryArray =  decodeHex(iv); //ivBinaryArray 长度为16个字节
//
//        //将明文按照utf-8编码转成二进制数组
//        String plainText = "[{\n" +
//                "\"nama_ljk\": \"PT TES\", \"jenis_ljk\": 1, \"domisili_ljk\": \"e317\", \"id_penyelenggara\": 0000\n" +
//                "}, {\n" +
//                "\"nama_ljk\": \"PT TES2\", \"jenis_ljk\": 3, \"domisili_ljk\": \"e317\", \"id_penyelenggara\": 0000\n" +
//                "}, {\n" +
//                "\"nama_ljk\": \"PT TESS\", \"jenis_ljk\": 3, \"domisili_ljk\": \"e317\", \"id_penyelenggara\": 0000\n" +
//                "}]";//明文
//        byte[] plainTextUtf8Array = plainText.getBytes("utf-8");
//
//        //加密
//        byte[] encryptedData = encrypt(plainTextUtf8Array, passwordBinaryArray, ivBinaryArray);
//        System.out.println("加密后的内容:" + Arrays.toString(encryptedData));
//        System.out.println("加密后的内容:" + new String(encryptedData));
//        //解密
//        byte[] decryptedData = decrypt(encryptedData, passwordBinaryArray, ivBinaryArray);
//        System.out.println("解密后的内容:" + Arrays.toString(decryptedData));
//        System.out.println("解密后的内容:" + new String(decryptedData, "utf-8"));
//
//        String s10 = "mvtBCTIyPDR+m/W07shfrDNn43Hcw7hllM/dgf8nws GGGqmKWlPo2egyeIWOEzeNGI9zIqDYGfMOtIv3XecI 1/NgRw7DIEOQVDSOzbg3BfQ /pPuup8CISiUr0zeGzyx jsO1qMO7o1ftdOTJ7N+HekTPQr7L7ZVR7Vb9Z2cia2b QZyrilr/JtHV4fbvoTt79g7+ml0pOiD2+VN+hahItxn+2c 5gAUUDr23qA9+fe/xSIrIPEBPwTFl070LWkbULzzTVm AAnec25Oo2t78yhoYGg==" + "::IV";
//        System.out.println(Base64Utils.encode(s10.getBytes()));
//
//        String s5 =  new String(Base64Utils.decode("bHMzQXJmTSt3TElxc08zRlFIa1BRYlIxOXcvMyt2K2tDMHorZkZ3NlYwa3U5NnU5SXpQdXR1d09JdVY 4QnN"));
//        System.out.println(s5);
//    }

    /**
     *   加密json字符串
     * */
    public static String encryptJsonData(String jsonData,OJKConfig config){

       try {
           String password = config.getAesKey();
           String iv = config.getIv();

           byte[] passwordBinaryArray = decodeHex(password); //passwordBinaryArray 长度为16个字节
           byte[] ivBinaryArray =  decodeHex(iv); //ivBinaryArray 长度为16个字节

           //将明文按照utf-8编码转成二进制数组
           byte[] plainTextUtf8Array = jsonData.getBytes("utf-8");

           //AES 加密
           byte[] encryptedData = encrypt(plainTextUtf8Array, passwordBinaryArray, ivBinaryArray);
           String aesResult = new String(Base64.getEncoder().encode(encryptedData));
           log.info("aes加密结果："+aesResult);

           //拼接字符串后 Base64加密
           String base64 = aesResult + "::IV";
           String base64Str =  Base64Utils.encode(base64.getBytes());
           log.info("base64加密结果："+base64Str);

           return base64Str;
       }catch (Exception e){
           log.error("ojk数据上传失败",e);
       }
       return "";
    }
}