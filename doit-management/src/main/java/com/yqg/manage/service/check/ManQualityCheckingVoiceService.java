package com.yqg.manage.service.check;

import com.yqg.common.models.PageData;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.DESUtils;
import com.yqg.manage.dal.collection.QualityCheckingVoiceDaoProvider;
import com.yqg.manage.service.check.request.DownloadFileEntity;
import com.yqg.manage.service.check.request.QualityCheckingVoiceRequest;
import com.yqg.manage.service.check.response.QualityCheckingVoiceResponse;
import com.yqg.manage.util.ZipUtils;
import com.yqg.service.third.infinity.InfinityService;
import com.yqg.service.util.ImageUtil;
import com.yqg.service.util.LoginSysUserInfoHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @program: microservice
 * @description: 质检语音Service
 * @author: 许金泉
 * @create: 2019-04-03 10:22
 **/
@Service
@Slf4j
public class ManQualityCheckingVoiceService {

    @Autowired
    private QualityCheckingVoiceDaoProvider qualityCheckingVoiceDao;

    @Autowired
    private InfinityService infinityService;

    @Value("${upload.imagePath}")
    private String imagePath;

    /**
     * @Description: 查询质检语音信息
     * @Param: [request]
     * @return: java.util.List<com.yqg.manage.service.check.response.QualityCheckingVoiceResponse>
     * @Author: 许金泉
     * @Date: 2019/4/3 13:55
     */
    public PageData getQualityCheckingVoiceList(QualityCheckingVoiceRequest request) {
        if (StringUtils.isNotBlank(request.getPhone())) {
            request.setPhone(CheakTeleUtils.telephoneNumberValid2(request.getPhone()));
        }
        List<QualityCheckingVoiceResponse> resultData = qualityCheckingVoiceDao.getQualityCheckingVoiceList(request).stream().map(item -> {
            QualityCheckingVoiceResponse checkingVoiceResponse = new QualityCheckingVoiceResponse();
            BeanUtils.copyProperties(item, checkingVoiceResponse);
            checkingVoiceResponse.setAttachmentPathUrl(StringUtils.isEmpty(item.getAttachmentSavePath())
            ? null : imagePath + ImageUtil.encryptUrl(item.getAttachmentSavePath()) + "&sessionId="
                    + LoginSysUserInfoHolder.getUsrSessionId());
            return checkingVoiceResponse;
        }).collect(Collectors.toList());
        PageData<List<QualityCheckingVoiceResponse>> result = new PageData<>();
        result.setPageNo(request.getPageNo());
        result.setPageSize(request.getPageSize());
        result.setData(resultData);
        result.setRecordsTotal(this.qualityCheckingVoiceDao.getQualityCheckingVoiceCount(request));
        return result;
    }

    /**
     * @Description: 查询质检语音信息
     * @Param: [uuidList] uuid集合以,隔开
     * @return: java.util.List<com.yqg.manage.service.check.response.downloadVoiceFile>
     * @Author: 许金泉
     * @Date: 2019/4/3 13:55
     */
    public void downloadVoiceFile(List<String> uuidList, HttpServletResponse response) {
        if (CollectionUtils.isEmpty(uuidList)) {
            return;
        }
        List<String> attachmentSavePathByUuidList = qualityCheckingVoiceDao.getAttachmentSavePathByUuidList(uuidList);
        if (CollectionUtils.isEmpty(attachmentSavePathByUuidList)) {
            return;
        }
        attachmentSavePathByUuidList = attachmentSavePathByUuidList.stream().filter(u -> StringUtils.isNotBlank(u)).collect(Collectors.toList());
        int size = attachmentSavePathByUuidList.size();

        List<DownloadFileEntity> fileNameAndStream = Collections.synchronizedList(new ArrayList<>(size));
        ExecutorService executorService = Executors.newFixedThreadPool(size);
        CountDownLatch countDownLatc = new CountDownLatch(size);
        // 多线程并发下载文件
        attachmentSavePathByUuidList.forEach((String item) -> {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    DownloadFileEntity downloadFileEntity = downloadFileStream(imagePath + ImageUtil.encryptUrl(item) + "&sessionId="
                            + LoginSysUserInfoHolder.getUsrSessionId());
                    if (downloadFileEntity != null) {
                        fileNameAndStream.add(downloadFileEntity);
                    }
                    countDownLatc.countDown();
                }
            });
        });
        try {
            countDownLatc.await();
        } catch (InterruptedException e) {
            log.error("CountDownLatch失败", e);
        }
        if (fileNameAndStream.isEmpty()) {
            return;
        }
        if (size == 1) {
            DownloadFileEntity downloadFileEntity = fileNameAndStream.get(0);
            InputStream inputStream = downloadFileEntity.getInputStream();
            try {
                byte[] buf = new byte[inputStream.available()];
                int len;
                ServletOutputStream outputStream1 = response.getOutputStream();
                while ((len = inputStream.read(buf)) != -1) {
                    outputStream1.write(buf, 0, len);
                }
                outputStream1.write(buf);
                response.setHeader("Content-Disposition", "attachment; filename=" + downloadFileEntity.getFileName());
                response.setContentType(downloadFileEntity.getContentType());
                outputStream1.close();
                outputStream1.flush();
            } catch (IOException e) {
                log.error("单文件写入失败", e);
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("关闭文件流出错", e);
                }
            }
            return;
        }

        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            response.setContentType("application/x-zip-compressed");
            // 当前时间+四个随机数 组成文件名
            String fileName = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()) + ".zip";
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        } catch (IOException e) {
            log.error("设置响应流失败", e);
        }
        Map<String, InputStream> collect = fileNameAndStream.stream().collect(Collectors.toMap(DownloadFileEntity::getFileName, DownloadFileEntity::getInputStream));
        ZipUtils.toZip(collect, outputStream);
        try {
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            log.error("关闭流失败", e);
        }
    }

    private DownloadFileEntity downloadFileStream(String urlStr) {

        try {
            DownloadFileEntity fileEntity = new DownloadFileEntity();
            fileEntity.setFileName(urlStr.substring(urlStr.lastIndexOf("/") + 1));
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置超时间为3秒s
            conn.setConnectTimeout(3 * 1000);
            fileEntity.setInputStream(conn.getInputStream());
            fileEntity.setContentType(conn.getContentType());
            return fileEntity;
        } catch (MalformedURLException e) {
            log.error("URL路径有问题", e);
            return null;
        } catch (IOException e) {
            log.error("下载文件报错", e);
            return null;
        }
    }

}
