package com.yqg.service.third.upload;

import com.yqg.RiskApplication;
import com.yqg.common.utils.Base64Utils;
import com.yqg.service.externalChannel.service.CheetahUploadService;
import com.yqg.service.third.upload.response.UploadResultInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RiskApplication.class)
@WebAppConfiguration
@Slf4j
public class UploadServiceTest {

    @Autowired
    private UploadService uploadService;

    @Autowired
    private CheetahUploadService cheetahUploadService;

    @Test
    public void uploadFile() throws URISyntaxException, IOException {
//        String fileUrl = "http://t2.hddhhn.com/uploads/tu/201612/6/4hgqkszdmpd.jpg";
//        Optional<UploadResultInfo> aNull = uploadService.downloadFileAndUpload(fileUrl, "");
//        System.out.println(aNull.get());
    }

    @Test
    public void uploadImages() throws URISyntaxException, IOException {
        String fileName = "4hgqkszdmpd.jpg";
        String fileUrl = "http://t2.hddhhn.com/uploads/tu/201612/6/4hgqkszdmpd.jpg";
        String base64Img = Base64Utils.getBase64ImgFromRemoteUrl(fileUrl);
        int suffixIndex = fileName.lastIndexOf(".");
        String fileSuffix = suffixIndex >= 0 ? fileName.substring(suffixIndex, fileName.length()) : ".jpg";
        UploadResultInfo fileUpload = uploadService.uploadBase64Img("null", base64Img, fileSuffix, fileSuffix);
        System.out.println(fileUpload);
    }


}