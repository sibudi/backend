package com.yqg.service.externalChannel.service;

import com.yqg.common.utils.Base64Utils;
import com.yqg.common.utils.StringUtils;
import com.yqg.service.third.upload.UploadService;
import com.yqg.service.third.upload.response.UploadResultInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by wanghuaizhou on 2018/12/27.
 */
@Service
@Slf4j
public class CheetahUploadService {


    @Autowired
    private UploadService uploadService;

    @Transactional(rollbackFor = Exception.class)
    public String uploadImages(MultipartFile file, String fileName)
            throws Exception {

        if (file == null || file.isEmpty() || StringUtils.isEmpty(fileName)) {
            throw new IllegalArgumentException("invalid param");
        }
        String base64Img = Base64Utils.getBase64ImgFromRemoteFile(file);
        int suffixIndex = fileName.lastIndexOf(".");
        String fileSuffix =
                suffixIndex >= 0 ? fileName.substring(suffixIndex, fileName.length()) : ".jpg";

        UploadResultInfo fileUpload = uploadService
                .uploadBase64Img("null", base64Img,  fileSuffix,
                          fileSuffix);

        return fileUpload.getData();
    }
}
