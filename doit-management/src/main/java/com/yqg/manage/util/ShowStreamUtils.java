package com.yqg.manage.util;

import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.service.alicloud.AlicloudServiceAdapter;
import com.yqg.service.user.service.UserAttachmentInfoService;
import com.yqg.service.util.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Author: tonggen
 * Date: 2019/4/6
 * time: 2:13 PM
 */
@Slf4j
@Component
public class ShowStreamUtils {

    @Autowired
    private UserAttachmentInfoService userAttachmentInfoService;
    
    /**
     * 根据类型的不同 直接显示在浏览器上
     * @param response
     * @param path for example /MyUpload/M/X/1341413431.png
     * @throws IOException
     */
    public void showStreamOnBrowser(HttpServletResponse response, String path) throws IOException {

        if (StringUtils.isEmpty(path)) {
            return;
        }
        log.info("showStreamOnBrowser - decrypt before path is " + path);
        if (!path.contains("MyUpload")) {
            path = ImageUtil.decryptUrl(path);
        }
        log.info("showStreamOnBrowser - decrypt after path is " + path);

        //path Must include file suffix
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
        try (OutputStream os = response.getOutputStream()) { 
            byte[] streamResult = userAttachmentInfoService.getAttachmentStream(path);
            if (streamResult != null) {
                os.write(streamResult);
            }
        }
    }
}
