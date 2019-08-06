package com.yqg.manage.service.system;

import com.yqg.common.utils.StringUtils;
import com.yqg.service.third.upload.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Author: tonggen
 * Date: 2019/5/30
 * time: 2:27 PM
 */
@Service
@Slf4j
public class DataFixService {


    @Autowired
    private UploadService uploadService;

    public String uploadVoiceCheck(String fileName) {
        String fileNameMp3 = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()) + ".mp3";
        final String[] result = new String[1];
        uploadService.downloadFileAndUpload(fileName,fileNameMp3).ifPresent(file -> {
            if (StringUtils.isNotBlank(file.getData())) {
                result[0] = file.getData();
            } else {
                log.warn(file.toString());
            }
        });
        return result[0];
    }
}
