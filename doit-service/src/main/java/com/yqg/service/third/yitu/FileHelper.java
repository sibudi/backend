package com.yqg.service.third.yitu;

// import org.apache.commons.codec.binary.Base64;

// import java.io.*;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

/**
 * 
 * @author Jacob
 *
 */
//ahalim: this code is unused
public class FileHelper {

    //static Logger logger = LoggerFactory.getLogger(FileHelper.class);

    //ahalim: Remark unused method
    // /**
    //  * ???????
    //  * 
    //  * @param fileName
    //  * @return ????????
    //  */
    // public static String readFile(String fileName) {

    //     String result = "";
    //     File file = new File(fileName);
    //     if (file.exists()) {
    //         Reader reader = null;
    //         try {
    //             // ???????
    //             reader = new InputStreamReader(new FileInputStream(file));
    //             int tempchar;
    //             while ((tempchar = reader.read()) != -1) {
    //                 result = result + (char) (tempchar);
    //             }
    //             reader.close();
    //         } catch (Exception e) {
    //             e.printStackTrace();
    //         }
    //     } else {
    //         System.out.println("No such file");
    //     }

    //     return result;
    // }

    //ahalim: Remark unused method
    // /**
    //  * ?????
    //  * 
    //  * @param content
    //  *            , ????
    //  * @param filepath
    //  *            , ????
    //  */
    // public static void saveFile(final String content, final String filepath) {

    //     try {
    //         FileWriter fw = new FileWriter(filepath, true);
    //         BufferedWriter bw = new BufferedWriter(fw);
    //         bw.write(content);
    //         bw.close();
    //         fw.close();
    //     } catch (IOException e) {
    //         // TODO Auto-generated catch block
    //         e.printStackTrace();
    //     }
    // }

    //ahalim: Remark unused method
    // /**
    //  * ??????????
    //  * 
    //  * @param fileName
    //  * @return ??????????
    //  */
    // public static byte[] readBinaryFile(String fileName) {
    //     byte[] result = null;
    //     byte[] tempBytes = new byte[256];
    //     File file = new File(fileName);
    //     if (file.exists()) {
    //         System.out.println("Get image from local folder");
    //         try {
    //             // ???????
    //             int byteread = 0;
    //             InputStream in = new FileInputStream(file);
    //             while ((byteread = in.read(tempBytes)) != -1) {
    //                 result = new byte[byteread];
    //                 System.arraycopy(tempBytes, 0, result, 0, byteread);
    //             }
    //             in.close();
    //         } catch (Exception e) {
    //             e.printStackTrace();
    //         }
    //     } else {
    //     }
    //     return result;
    // }

    //ahalim: Remark unused method
    // /**
    //  * ????????
    //  * 
    //  * @param content
    //  *            , ????
    //  * @param filepath
    //  *            , ????
    //  */
    // public static void saveBinaryFile(final byte[] content,
    //         final String filepath) {
    //     File file = new File(filepath);
    //     try {
    //         BufferedOutputStream fos = new BufferedOutputStream(
    //                 new FileOutputStream(file));
    //         fos.write(content, 0, content.length);
    //         fos.flush();
    //         fos.close();
    //     } catch (IOException e) {
    //         // TODO Auto-generated catch block
    //         e.printStackTrace();
    //     }
    // }

    //ahalim: Remark unused method
    // /**
    //  * Read from Local folder or from Oss
    //  * 
    //  * @param filePath usrAttachmentInfo attachmentSavePath
    //  * @return Base64 encoded string
    //  * @throws IOException 
    //  */
    // public static String getImageBase64Content(String filePath) {
    //     File imgFile = new File(filePath);
    //     byte[] bytes = null;
    //     if(imgFile.exists() && !imgFile.isDirectory()) {
    //         logger.info("Get image from local folder");
    //         try {
    //             InputStream is = new FileInputStream(imgFile);
    //             long length = imgFile.length();
    //             bytes = new byte[(int) length];

    //             int offset = 0, numRead = 0;
    //             while (offset < bytes.length
    //                     && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
    //                 offset += numRead;
    //             }
    //             is.close();
    //         } catch (IOException e) {
    //             e.printStackTrace();
    //         }
    //         return Base64.encodeBase64String(bytes);
    //     } else {
    //     }
    // }
    
    //ahalim: Remark unused method
    // /**
    //  * @Title: GetImageStrFromUrl
    //  * @Description: TODO(??????????Base64???)
    //  * @param imgURL ??????
    //  * @return Base64???
    //  */
    // public static String getImageStrFromUrl(String imgURL) {
    //     byte[] data = null;
    //     try {
    //         // ??URL
    //         URL url = new URL(imgURL);
    //         // ????
    //         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    //         conn.setRequestMethod("GET");
    //         conn.setConnectTimeout(5 * 1000);

    //         InputStream inStream = conn.getInputStream();
    //         ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    //         byte[] buffer = new byte[1024];
    //         int len = 0;
    //         while( (len=inStream.read(buffer)) != -1 ){
    //             outStream.write(buffer, 0, len);
    //         }
    //         data = outStream.toByteArray();
    //         inStream.close();
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    //     // ??Base64???????????
    //     return Base64.encodeBase64String(data);
    // }
}
