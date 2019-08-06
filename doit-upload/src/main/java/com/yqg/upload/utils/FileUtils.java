package com.yqg.upload.utils;

import com.yqg.common.utils.MD5Util;
import com.yqg.common.utils.UUIDGenerateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.codec.binary.Base64;

import java.io.*;


public class FileUtils {

    static Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * ?????Resources????????
     *
     * @param filePath
     * @return
     */
    public static String readFile(String filePath) {
        try {
            StringBuffer sb = new StringBuffer();
            FileReader freader = new FileReader(new File(filePath));
            BufferedReader buffer = new BufferedReader(freader);
            String str_line = buffer.readLine();
            while (str_line != null) {
                sb.append(str_line);
                sb.append("\n");
                str_line = buffer.readLine();
            }
            buffer.close();
            freader.close();

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * ????????
     *
     * @return
     */
    public static String getFileName(String name) {
        if (name.contains("."))
            return MD5Util.md5LowerCase(UUIDGenerateUtil.uuid()) + name.substring(name.lastIndexOf("."), name.length());
        return MD5Util.md5LowerCase(UUIDGenerateUtil.uuid());
    }

    /**
     * ???????????(??)??????
     *
     * @param name
     * @param fileType
     * @return
     */
    public static String getFilePath(String name, String fileType,String rootPath) {

        String fileTypePath=fileType.split("[.]")[0];

        String path1 = getFilePath(name);

        String path2 = getFilePath(UUIDGenerateUtil.uuid());

        String result = rootPath + File.separator + fileTypePath.toUpperCase() + File.separator
                + path1 + path2 + File.separator + name;
        return result.replace("//", "/");
    }

    public static String getFilePath(String str) {

        int code = str.hashCode();

        return FilePath.getCode((code % 26 > 0 ?
                code % 26 : 0 - code % 26) + 1).getPath();
    }


    /**
     * ????base64
     *
     * @param imgFilePath
     * @return
     */
    public static String GetImageStr(String imgFilePath) {
        byte[] data = null;

        // ????????
        try {
            InputStream in = new FileInputStream(imgFilePath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ?????Base64??
        return Base64.encodeBase64String(data);// ??Base64???????????
    }

    /**
     * base64 ????
     *
     * @param imgStr
     * @return
     */
    public static byte[] GenerateImage(String imgStr) {
        if (imgStr == null) // ??????
            return null;
        try {
            // Base64??
            byte[] bytes = Base64.decodeBase64(imgStr);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {// ??????
                    bytes[i] += 256;
                }
            }
//            ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(bytes);
            return bytes;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
