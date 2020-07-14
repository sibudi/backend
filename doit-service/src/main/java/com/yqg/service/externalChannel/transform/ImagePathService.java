package com.yqg.service.externalChannel.transform;

import com.yqg.common.enums.user.UsrAttachmentEnum;
import com.yqg.common.utils.Base64Utils;
import com.yqg.common.utils.StringUtils;
import com.yqg.service.third.upload.UploadService;
import com.yqg.service.third.upload.response.UploadResultInfo;
import com.yqg.user.dao.UsrAttachmentInfoDao;
import com.yqg.user.entity.UsrAttachmentInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/*****
 * @Author zengxiangcai
 * Created at 2018/6/22
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Service
@Slf4j
public class ImagePathService {

    @Autowired
    private UploadService uploadService;

    @Autowired
    private UsrAttachmentInfoDao usrAttachmentInfoDao;

    /****
     * Download the remote image URL to the local server  (call doit-upload)
     *   and return the local server's URL
     * 
     * Called by:
     * 1. AdditionalInfoExtractor.java (used by Cash2AdditionalInfoController.java)
     * 2. BaseInfoExtractor.java (used by Cash2BaseInfoController.java)
     * 3. CheetahBaseInfoExtractor.java (unused code, remarked)
     * 4. OrderImageCheckTask.java scheduler (currently disabled)
     * @param remoteImgUrl
     * @return
     */
    public String getLocalFileUrl(String remoteImgUrl, UsrAttachmentEnum type) {
        if (StringUtils.isEmpty(remoteImgUrl)) {
            return null;
        }
        if(remoteImgUrl.startsWith("http:")||remoteImgUrl.startsWith("https:")){
            String base64Img = Base64Utils.getBase64ImgFromRemoteUrl(remoteImgUrl);
            if(StringUtils.isEmpty(base64Img)){
                //Compatible with the case where the picture cannot be obtained during timeout
                log.info("can not get image, return the original url, {}",remoteImgUrl);
                return remoteImgUrl;
            }
            int suffixIndex = remoteImgUrl.lastIndexOf(".");
            String fileSuffix =
                    suffixIndex >= 0 ? remoteImgUrl.substring(suffixIndex, remoteImgUrl.length()) : ".jpg";

            UploadResultInfo fileUpload = uploadService
                    .uploadBase64Img("null", base64Img, type.name() + fileSuffix,
                            type.name() + fileSuffix);
            return fileUpload.getData();
        }else{
            String base64Img = remoteImgUrl;
            String fileSuffix = ".jpg";

            UploadResultInfo fileUpload = uploadService
                    .uploadBase64Img("null", base64Img, type.name() + fileSuffix,
                            type.name() + fileSuffix);
            return fileUpload.getData();
        }

    }

    // ahalim: remark unused code
    // public byte[] getImageStream(String imgUrl) {
    //     BufferedInputStream bis = null;
    //     HttpURLConnection httpUrl = null;
    //     try {
    //         URL url = new URL(imgUrl);
    //         httpUrl = (HttpURLConnection) url.openConnection();
    //         httpUrl.setConnectTimeout(2000);
    //         httpUrl.setReadTimeout(60000);
    //         httpUrl.connect();
    //         bis = new BufferedInputStream(httpUrl.getInputStream());
    //         return IOUtils.toByteArray(bis);
    //     } catch (Exception e) {
    //         log.error("fetch image exception，imgUrl: " + imgUrl, e);
    //         return null;
    //     } finally {
    //         if (httpUrl != null) {
    //             httpUrl.disconnect();
    //         }
    //         if(bis!=null){
    //             try {
    //                 bis.close();
    //             } catch (IOException e) {
    //                 log.error("close bis error",e);
    //             }
    //         }
    //     }
    // }

    public List<UsrAttachmentInfo> getErrorImagePathListForCashCash(){
        return usrAttachmentInfoDao.getErrorImagePathListForCashCash();
    }
}
