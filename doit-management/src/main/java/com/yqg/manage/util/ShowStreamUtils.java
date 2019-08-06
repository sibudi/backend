package com.yqg.manage.util;

import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.service.util.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Author: tonggen
 * Date: 2019/4/6
 * time: 2:13 PM
 */
@Slf4j
public class ShowStreamUtils {

    /**
     * 根据类型的不同 直接显示在浏览器上
     * @param response
     * @param path for example /MyUpload/M/X/1341413431.png
     * @throws IOException
     */
    public static void showStreamOnBrowser(HttpServletResponse response, String path) throws IOException {

        if (StringUtils.isEmpty(path)) {
            return;
        }
        log.info("decrypt before path is " + path);
        if (!path.contains("MyUpload")) {
            path = ImageUtil.decryptUrl(path);
        }
        log.info("decrypt after path is " + path);

        //path 必须包括文件后缀
        String suffix = "";
        if (path.split("\\.").length > 1) {
            suffix = path.split("\\.")[1];
        }
        if ("jpg".equalsIgnoreCase(suffix) || "jpeg".equalsIgnoreCase(suffix)) {
            response.setContentType("image/jpeg");
        } else if ("png".equalsIgnoreCase(suffix)) {
            response.setContentType("image/png");
        } else if ("mp3".equalsIgnoreCase(suffix)) {
            response.setContentType("audio/mp3");
        } else {
            response.setContentType("image/jpeg");
        }
        OutputStream os = response.getOutputStream();
        InputStream stream = new FileInputStream(path);
        try {
            os.write(IOUtils.toByteArray(stream));
        } catch (FileNotFoundException e) {

        } finally {
            os.flush();
            os.close();
        }
    }
}
