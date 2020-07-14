package com.yqg.service.user.service;

import com.yqg.common.utils.StringUtils;
import com.yqg.service.alicloud.AlicloudServiceAdapter;
import com.yqg.user.dao.UsrAttachmentInfoDao;
import com.yqg.user.entity.UsrAttachmentInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserAttachmentInfoService {
    
    @Autowired
    private UsrAttachmentInfoDao usrAttachmentInfoDao;

    @Autowired
    private AlicloudServiceAdapter alicloudServiceAdapter;
    
    public List<UsrAttachmentInfo> getAttachmentListByUserId(String userUuid){
        if(StringUtils.isEmpty(userUuid)){
            log.info("the userId is empty");
            return new ArrayList<>();
        }
        UsrAttachmentInfo searchParam = new UsrAttachmentInfo();
        searchParam.setDisabled(0);
        searchParam.setUserUuid(userUuid);
        return usrAttachmentInfoDao.scan(searchParam);
    }

    /**
     * Get attachment stream either from:
     * 1. Local folder attachmentSavePath
     * 2. OSS
     * 3. Remote url attachmentUrl
     * Called by:
     * 1. DigisignParamService.java
     *    getEkycIdentityValidationData.java -> DigisignService.java verifyEkycFromAsli()
     * 2. DigisignService.java
     *    a. register()
     *    b. registerWithKYC()
     */
    public byte[] getAttachmentStream(UsrAttachmentInfo usrAttachmentInfo) {
        if (usrAttachmentInfo == null) {
            return null;
        }
        byte[] attachmentStream = getAttachmentStream(usrAttachmentInfo.getAttachmentSavePath());
        if (attachmentStream == null) {
            attachmentStream = getAttachmentStreamFromUrl(usrAttachmentInfo.getAttachmentUrl());
        }
        return attachmentStream;
    }
    /**
     * Get attachment stream with base64 encoded
     * Called by:
     * 1. usrBaseInfoService.java getAndUpdateUser
     *    a. UsrBaseInfoController.java -> API /userBaseInfo/getAndUpdateUser
     *    b. BaseInfoService.java addBaseInfo() -> Cash2BaseInfoController.java -> API /external/cash2/baseInfo
     *    c. CheetahBaseInfoService.java addBaseInfo() -> CheetahBaseInfoController.java -> API /cash-loan/v1
     * 2. usrService.java submitCertificationInfo
     *    a. UsrController.java -> API /users/submitCertificationInfo
     *    b. AdditionalInfoservice.java -> Cash2BaseInfoController.java  -> API /external/cash2/additional-info
     * @param attachmentSavePath
     * @return
     */
    public String getBase64AttachmentStream(String attachmentSavePath) {
        return Base64.encodeBase64String(getAttachmentStream(attachmentSavePath));
    }

    /**
     * Get attachment stream either from:
     * 1. Local folder attachmentSavePath
     * 2. OSS
     */
    public byte[] getAttachmentStream(String attachmentSavePath) {
        File f = new File(attachmentSavePath);
        if (f.exists() && !f.isDirectory()) {
            log.info("getAttachmentStream - Get image from local folder");
            try (BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(f))) {
                return IOUtils.toByteArray(inStream);
            } catch (Exception e) {
                log.error("getAttachmentStream local folder- Error. Path: " + attachmentSavePath);
            }
        } else {
            try {
                if(alicloudServiceAdapter.isOssObjectExist(attachmentSavePath)) {
                    log.info("getAttachmentStream - Get image from oss");
                    return alicloudServiceAdapter.getOssStreamData(attachmentSavePath);
                }
            } catch (Exception e) {
                log.error("getAttachmentStream oss - Error. Path: " + attachmentSavePath);
            }
        }
        log.info("getAttachmentStream - Attachment Not Found");
        return null;    //Attachment not found
    }

    /**
     * Get attachment stream either from remote url attachmentUrl
     */
    private byte[] getAttachmentStreamFromUrl(String attachmentUrl) {
        log.info("getAttachmentStream - Get image from attachment url");
        BufferedInputStream bis = null;
        HttpURLConnection httpUrl = null;
        try {
            URL url = new URL(attachmentUrl);
            httpUrl = (HttpURLConnection) url.openConnection();
            httpUrl.setConnectTimeout(2000);
            httpUrl.setReadTimeout(60000);
            httpUrl.connect();
            bis = new BufferedInputStream(httpUrl.getInputStream());
            return IOUtils.toByteArray(bis);
        } catch (Exception e) {
            log.error("getAttachmentStreamFromUrl - Failed on url: " + attachmentUrl, e);
            return null;
        } finally {
            if (httpUrl != null) {
                httpUrl.disconnect();
            }
            if(bis!=null){
                try {
                    bis.close();
                } catch (IOException e) {
                    log.error("close bis error",e);
                }
            }
        }
    }

    public static void main(String[] args) {
        File f = new File("http://www.do-it.id/MyUpload/ID_CARD/H/Z/4507e8e3fcf24dd1fb3eb693db3bb653.jpg");
        System.err.println(f.exists());
    }
}
