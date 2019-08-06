package com.yqg.service.user.service;

import com.yqg.common.utils.StringUtils;
import com.yqg.service.externalChannel.transform.ImagePathService;
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
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserAttachmentInfoService {
    @Autowired
    private UsrAttachmentInfoDao usrAttachmentInfoDao;
    @Autowired
    private ImagePathService imagePathService;

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

    public byte[] getAttachmentStream(UsrAttachmentInfo usrAttachmentInfo) {
        if (usrAttachmentInfo == null) {
            return null;
        }
        File f = new File(usrAttachmentInfo.getAttachmentSavePath());
        if (f.exists()) {
            try (BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(f))) {
                return IOUtils.toByteArray(inStream);
            } catch (Exception e) {
                log.error("get file stream error, path: " + f.getPath());
            }
            return null;

        } else {
            return imagePathService.getImageStream(usrAttachmentInfo.getAttachmentUrl());
        }

    }

    public static void main(String[] args) {
        File f = new File("http://www.do-it.id/MyUpload/ID_CARD/H/Z/4507e8e3fcf24dd1fb3eb693db3bb653.jpg");
        System.err.println(f.exists());
    }
}
