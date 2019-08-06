package com.yqg.service.third.yitu;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jacob
 * ????HTTP?????????
 */
public class HttpRequestHelper {

    public static int connectTimeOut = 10000;
    public static int readTimeOut = 10000;
    private static final char[] toDigit = ("0123456789ABCDEF").toCharArray();
    public static PublicKey publicKey;

    /**
     * ????byte??
     * 
     * @param a
     * @param b
     * @return
     */
    public static byte[] mergeArray(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    /**
     * base16??
     */
    public static String encode(byte[] b) {
        char[] chars = new char[2 * b.length];
        int j = 0;

        for (int i = 0; i < b.length; ++i) {
            byte bits = b[i];

            chars[j++] = toDigit[((bits >>> 4) & 0xF)];
            chars[j++] = toDigit[(bits & 0xF)];
        }

        return new String(chars);
    }

    /**
     * ??Signature
     */
    public static String generateSignature(String accessKey, String MD5String,
            String userDefinedContent)
            throws Exception {

        String result = null;

        // ??unix???
        int unixTime = (int) (System.currentTimeMillis() / 1000L);
        byte[] unixTimeArray = ByteBuffer.allocate(4).putInt(unixTime).array();

        // ?????
        SecureRandom sr = new SecureRandom();
        byte[] rndBytes = new byte[8];
        sr.nextBytes(rndBytes);

        // ??Signature
        byte[] temp = mergeArray(accessKey.getBytes(Charset.forName("UTF-8")),
                EncryptionHelper.MD5Helper
                        .md5(MD5String).getBytes(Charset.forName("UTF-8")));
        temp = mergeArray(temp, unixTimeArray);
        temp = mergeArray(temp, rndBytes);
        temp = mergeArray(temp, userDefinedContent.getBytes(Charset.forName("UTF-8")));

        // RSA??
        Security.addProvider(new BouncyCastleProvider());
        try {
            result = encode(EncryptionHelper.RSAHelper.encrypt(temp, publicKey));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 16????
     * 
     * @param bytes
     *            , ??????
     * @return ??16????????
     */
    public static String hexEncode(byte[] bytes) {
        char[] chars = new char[2 * bytes.length];
        int j = 0;

        for (int i = 0; i < bytes.length; ++i) {
            byte bits = bytes[i];

            chars[j++] = toDigit[((bits >>> 4) & 0xF)];
            chars[j++] = toDigit[(bits & 0xF)];
        }

        return new String(chars);
    }

    /**
     * ??Signature
     * 
     * @param accessKey
     *            , access Key
     * @param bodyString
     *            , HTTP ????
     * @param userDefinedContent
     *            , ???????????41??
     * @return ???????Signature
     * @throws EncryptionHelper.MD5Helper.Md5EncodingException
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     * @throws InvalidKeyException 
     */
    public static String generateSignature(PublicKey publicKey, String accessKey, String bodyString,
            String userDefinedContent)
            throws EncryptionHelper.MD5Helper.Md5EncodingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

        String result = null;

        // ??unix???
        int unixTime = (int) (System.currentTimeMillis() / 1000L);
        byte[] unixTimeArray = ByteBuffer.allocate(4).putInt(unixTime).array();

        // ?????
        SecureRandom sr = new SecureRandom();
        byte[] rndBytes = new byte[8];
        sr.nextBytes(rndBytes);

        // ??Signature
        byte[] signatureStr = mergeArray(accessKey.getBytes(Charset.forName("UTF-8")),
                EncryptionHelper.MD5Helper.md5(bodyString).getBytes(Charset.forName("UTF-8")));
        signatureStr = mergeArray(signatureStr, unixTimeArray);
        signatureStr = mergeArray(signatureStr, rndBytes);
        signatureStr = mergeArray(signatureStr,
                userDefinedContent.getBytes(Charset.forName("UTF-8")));

        // RSA??
        result = hexEncode(EncryptionHelper.RSAHelper.encrypt(signatureStr, publicKey));

        return result;
    }

    /**
     * ??HTTP POST??
     * 
     * @param url
     *            , ????
     * @param accessId
     *            , access Id
     * @param accessKey
     *            , access Key
     * @param bodyString
     *            , HTTP ????
     * @param userDefinedContent
     *            , ???????????41??
     * @return ??????
     * @throws SocketTimeoutException
     * @throws UnknownHostException
     * @throws IOException
     * @throws EncryptionHelper.MD5Helper.Md5EncodingException
     */
    public static String sendPost(PublicKey publicKey, String url, String accessId,
            String accessKey,
            String bodyString, String userDefinedContent) throws Exception {

        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";

        URL realUrl = new URL(url);
        // ???URL?????
        URLConnection conn = realUrl.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection) conn;
        // ?????????,??accessId?signature
        httpConn.setRequestProperty("accept", "*/*");
        httpConn.setRequestProperty("connection", "Keep-Alive");
        httpConn.setRequestProperty("Content-Type", "application/json");
        httpConn.setRequestProperty("x-access-id", accessId);
        httpConn.setRequestProperty("x-signature",
                generateSignature(publicKey, accessKey, bodyString, userDefinedContent));
        httpConn.setConnectTimeout(connectTimeOut);
        httpConn.setReadTimeout(readTimeOut);
        // ??POST??????????
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        httpConn.setUseCaches(false);
        httpConn.setRequestMethod("POST");

        httpConn.connect();
        // ??URLConnection????????
        out = new PrintWriter(httpConn.getOutputStream());
        // ??????
        out.print(bodyString);
        // flush??????
        out.flush();

        // System.out.println(bodyString);

        // ??BufferedReader??????URL???,??????200?????????
        if (httpConn.getResponseCode() != 200) {
            in = new BufferedReader(new InputStreamReader(httpConn.getErrorStream()));
        } else {
            in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
        }

        String line;
        while ((line = in.readLine()) != null) {
            result += line;
        }

        if (out != null) {
            out.close();
        }
        if (in != null) {
            in.close();
        }

        return result;
    }

    public static String sendPost(String url, String accessId, String signature, String bodyString)
            throws Exception {

        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";

        try {
            URL realUrl = new URL(url);
            // ???URL?????
            URLConnection conn = realUrl.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            // ?????????,??accessId?signature
            httpConn.setRequestProperty("accept", "*/*");
            httpConn.setRequestProperty("connection", "Keep-Alive");
            httpConn.setRequestProperty("Content-Type", "application/json");
            httpConn.setRequestProperty("x-access-id", accessId);
            httpConn.setRequestProperty("x-signature", signature);
            httpConn.setConnectTimeout(connectTimeOut);
            httpConn.setReadTimeout(readTimeOut);
            // ??POST??????????
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setUseCaches(false);
            httpConn.setRequestMethod("POST");

            httpConn.connect();
            // ??URLConnection????????
            out = new PrintWriter(httpConn.getOutputStream());
            // ??????
            out.print(bodyString);
            // flush??????
            out.flush();
            // ??BufferedReader??????URL???,??????200?????????
            if (httpConn.getResponseCode() != 200) {
                in = new BufferedReader(new InputStreamReader(httpConn.getErrorStream(), "UTF-8"));
            } else {
                in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
            }

            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (SocketTimeoutException e) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> errorData = new HashMap<String, Object>();
            errorData.put("rtn", -1);
            errorData.put("message", "Time out");
            result = mapper.writeValueAsString(errorData);
            // e.printStackTrace();
        } catch (UnknownHostException e) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> errorData = new HashMap<String, Object>();
            errorData.put("rtn", -1);
            errorData.put("message", "Wrong url");
            result = mapper.writeValueAsString(errorData);
        } catch (Exception e) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> errorData = new HashMap<String, Object>();
            errorData.put("rtn", -1);
            errorData.put("message", "Unknow error");
            result = mapper.writeValueAsString(errorData);
        }
        // ??finally???????????
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
}
