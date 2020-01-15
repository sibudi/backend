package com.yqg.scheduling;


import com.yqg.common.enums.user.UsrAttachmentEnum;
import com.yqg.service.externalChannel.transform.ImagePathService;
import com.yqg.service.user.service.UsrService;
import com.yqg.user.entity.UsrAttachmentInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;


@Component
@Slf4j
public class OrderImageCheckTask {
    @Autowired
    private ImagePathService imagePathService;
    @Autowired
    private UsrService usrService;

    /****
     * 每隔5分钟检查cashcash订单图片问题
     * 因网络问题偶尔CashCash的图片无法下载(timeout),需要扫描附件表定时重新获取图片
     */
    // @Scheduled(cron = "0 0/5 * * * ?")
    public void checkImage() {
        try {
            List<UsrAttachmentInfo> errorList = imagePathService.getErrorImagePathListForCashCash();
            if (CollectionUtils.isEmpty(errorList)) {
                return;
            }
            log.info("total error: {}",errorList.size());
            for (UsrAttachmentInfo item : errorList) {
                try {
                    log.info("start check userUuid: {}",item.getUserUuid());
                    UsrAttachmentEnum enumType = UsrAttachmentEnum.enumFromType(item.getAttachmentType());
                    String localUrl = imagePathService.getLocalFileUrl(item.getAttachmentUrl(), enumType);
                    if (item.getAttachmentUrl().equals(localUrl) || StringUtils.isEmpty(localUrl)) {
                        continue;
                    }
                    //更新图片url
                    usrService.insertAttachment(item.getUserUuid(), localUrl, item.getAttachmentType().toString());
                } catch (Exception e1) {
                    log.error("update cashcash error image path failed, userUuid =  " + item.getUserUuid(), e1);
                }
            }
        } catch (Exception e) {
            log.info("image url error", e);
        }
    }
}
