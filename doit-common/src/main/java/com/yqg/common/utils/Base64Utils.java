package com.yqg.common.utils;

import com.yqg.common.exceptions.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.IOUtils;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * BASE64???????
 * 
 * @author Jacob
 *
 */

@Slf4j
public class Base64Utils {

    /**
     * ?????????
     */
    private static final int CACHE_SIZE = 1024;

    /**
     * <p>
     * BASE64???????????
     * </p>
     * 
     * @param base64
     * @return
     * @throws Exception
     */
    public static byte[] decode(String base64)  {
        return Base64.decode(base64.getBytes());
    }

    /**
     * <p>
     * ????????BASE64???
     * </p>
     * 
     * @param bytes
     * @return
     * @throws Exception
     */
    public static String encode(byte[] bytes) {
        return new String(Base64.encode(bytes));
    }

    /**
     * <p>
     * ??????BASE64???
     * </p>
     * <p>
     * ???????????????
     * </p>
     * 
     * @param filePath ??????
     * @return
     * @throws IOException 
     * @throws Exception
     */
    public static String encodeFile(String filePath) throws IOException {
        byte[] bytes = fileToByte(filePath);
        return encode(bytes);
    }

    /**
     * <p>
     * BASE64???????
     * </p>
     * 
     * @param filePath ??????
     * @param base64 ?????
     * @throws IOException 
     * @throws Exception
     */
    public static void decodeToFile(String filePath, String base64) throws IOException {
        byte[] bytes = decode(base64);
        byteArrayToFile(bytes, filePath);
    }

    /**
     * <p>
     * ??????????
     * </p>
     * 
     * @param filePath ????
     * @return
     * @throws IOException 
     * @throws Exception
     */
    public static byte[] fileToByte(String filePath) throws IOException {
        byte[] data = new byte[0];
        File file = new File(filePath);
        if (file.exists()) {
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
            byte[] cache = new byte[CACHE_SIZE];
            int nRead = 0;
            while ((nRead = in.read(cache)) != -1) {
                out.write(cache, 0, nRead);
                out.flush();
            }
            out.close();
            in.close();
            data = out.toByteArray();
        }
        return data;
    }

    /**
     * <p>
     * ????????
     * </p>
     * 
     * @param bytes ?????
     * @param filePath ??????
     * @throws IOException 
     */
    public static void byteArrayToFile(byte[] bytes, String filePath) throws IOException {
        InputStream in = new ByteArrayInputStream(bytes);
        File destFile = new File(filePath);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        destFile.createNewFile();
        OutputStream out = new FileOutputStream(destFile);
        byte[] cache = new byte[CACHE_SIZE];
        int nRead = 0;
        while ((nRead = in.read(cache)) != -1) {
            out.write(cache, 0, nRead);
            out.flush();
        }
        out.close();
        in.close();
    }

    private static String baseUrl = "http://localhost:8082/";
    /**
     * 
     * @param destUrl
     * @return
     * @throws IOException
     */
    public static String generateBase64Content(String destUrl) throws IOException {
        BufferedInputStream bis = null;
        HttpURLConnection httpUrl = null;
        URL url = null;
        byte[] buf = new byte[1024];
        StringBuilder sBuilder = new StringBuilder();
        try {
            url = new URL(baseUrl + destUrl);
            httpUrl = (HttpURLConnection) url.openConnection();
            httpUrl.connect();
            bis = new BufferedInputStream(httpUrl.getInputStream());
            while ((bis.read(buf)) != -1) {
                sBuilder.append(new String(buf));
                buf = new byte[1024];
            }
            return Base64Utils.encode(sBuilder.toString().getBytes());
        } finally {
            if (bis != null) {
                bis.close();
            }
            if (httpUrl != null) {
                httpUrl.disconnect();
            }
        }
    }

    public static String getBase64ImgFromRemoteUrl(String imgUrl) {
        BufferedInputStream bis = null;
        HttpURLConnection httpUrl = null;
        try {
            URL url = new URL(imgUrl);
            httpUrl = (HttpURLConnection) url.openConnection();
            httpUrl.setConnectTimeout(2000);
            httpUrl.setReadTimeout(60000);
            httpUrl.connect();
            bis = new BufferedInputStream(httpUrl.getInputStream());
            byte[] data = IOUtils.toByteArray(bis);
            IOUtils.closeQuietly(bis);
            return Base64Utils.encode(data);
        } catch (Exception e) {
            log.error("fetch image exception，imgUrl: " + imgUrl, e);
            return null;
        } finally {
            if (httpUrl != null) {
                httpUrl.disconnect();
            }
        }
    }


    public static String getBase64ImgFromRemoteFile(MultipartFile file) {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(file.getInputStream());
            byte[] data = IOUtils.toByteArray(bis);
            IOUtils.closeQuietly(bis);
            return Base64Utils.encode(data);
        } catch (Exception e) {
            log.error("fetch image exception，file");
            return null;
        }
    }

    /**
     * 读取本地磁盘文件
     * @throws BadRequestException
     *
     */
    public static String getBase64Str(String path) {

        if (StringUtils.isEmpty(path)) {
            return "";
        }
        try {
            InputStream stream = new FileInputStream(path);
            return new BASE64Encoder().encode(IOUtils.toByteArray(stream)).replaceAll("\n", "");
        } catch (Exception e) {
            log.error("getBase64Str error", e);
        }
        return "";
    }

}
