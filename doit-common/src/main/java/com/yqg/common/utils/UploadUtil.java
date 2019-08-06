package com.yqg.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author  Jacob
 */
@SuppressWarnings("restriction")
public class UploadUtil {

    private static final Logger logger = LoggerFactory.getLogger(UploadUtil.class);

    private static final String uploadPath = "/imagedata/";

    /**
     * ????????
     * @param request
     * @param
     * @return
     * @throws IOException
     */
    public static List<String> uploadFiles(HttpServletRequest request) throws IOException {
        List<String> arrs = new ArrayList<>();
        // ?????????????
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        // ?? request ???????,??????
        if (multipartResolver.isMultipart(request)) {
            // ??????request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;

            // ??request???????
            Iterator<String> iter = multiRequest.getFileNames();

            while (iter.hasNext()) {
                // ?????????????????????
                int pre = (int) System.currentTimeMillis();
                // ??????
                MultipartFile file = multiRequest.getFile(iter.next());
                if (file != null) {
                    //?????????????
                    String myFileName = file.getOriginalFilename();
                    //????????,??????????????????
                    if (myFileName.trim() != "") {
                        logger.info(myFileName);
                        //?????
                        String tempFileName = file.getOriginalFilename().substring(
                                file.getOriginalFilename().lastIndexOf("."),
                                file.getOriginalFilename().length());
                        String fileType = getFileType(tempFileName);
                        //??????????
                        String fileName = UUIDGenerateUtil.uuid() + tempFileName;
                        //??????
                        String path = uploadPath + fileType + fileName;
                        File localFile = new File(path);
                        if (!localFile.exists()) {
                            localFile.mkdirs();
                        }
                        file.transferTo(localFile);
                        logger.info("????????:" + path);
                        arrs.add(path);
                    }
                }
                //???????????
                int finalTime = (int) System.currentTimeMillis();

                logger.info("??{0}", file.getName() + "????{1}", String.valueOf(finalTime - pre));
                break;
            }
        }
        return arrs;
    }

    /**
     * ???????????
     * @param suffix
     * @return
     */
    public static String getFileType(String suffix) {
        String fileType = "";
        suffix = suffix.substring(1, suffix.length());
        if (suffix.equals("png") || suffix.equals("jpg") || suffix.equals("jpeg")) {
            fileType = "image";
        } else if (suffix.equals("video")) {
            fileType = "video";
        } else if (suffix.equals("mp3")) {
            fileType = "mp3";
        } else if (suffix.equals("txt")) {
            fileType = "txt";
        } else {
            fileType = "other";
        }
        return "/" + fileType + "/";
    }

    /**
     * ??????????Base64???????
     * 
     * @param imgStr
     * @return
     * @throws IOException
     */
    public static String generateImage(String imgStr, String uploadPath) throws IOException {
        String imgFilePath = null;
        String fileName = null;
        OutputStream out = null;
        if (imgStr == null) //??????
            return null;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            //Base64??
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {//??????
                    b[i] += 256;
                }
            }
//            DateUtils.dateToDay() + 
            fileName = UUIDGenerateUtil.uuid() + ".jpeg";
            //??jpeg??
            imgFilePath = uploadPath + fileName;//??????
            out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            return fileName;
        } finally {
            if (out != null) {
                out.close();
            }

        }
    }



}
