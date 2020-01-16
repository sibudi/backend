package com.yqg.scheduling;

import com.yqg.service.check.QualityCheckingVoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Author: tonggen
 * Date: 2019/4/7
 * time: 3:29 PM
 */
@Slf4j
@Component
public class QualityVoiceScheduling {

    @Autowired
    private QualityCheckingVoiceService qualityCheckingVoiceService;

    /****
     * 每隔一分钟 更新语音外呼mp3文件
     *
     */

    // @Scheduled(cron = "0 */1 * * * ?")
    public void updateQualityVoiceRecord() throws Exception {
        log.info("update Quality0 voice Record start ========");
        qualityCheckingVoiceService.updateQualityVoiceRecord(0);
        log.info("update quality0 voice record end ========");
    }

    // @Scheduled(cron = "0 */1 * * * ?")
    public void updateQualityVoiceRecord1() throws Exception {
        log.info("update Quality1 voice Record start ========");
        qualityCheckingVoiceService.updateQualityVoiceRecord(1);
        log.info("update quality1 voice record end ========");
    }

    // @Scheduled(cron = "0 */1 * * * ?")
    public void updateQualityVoiceRecord2() throws Exception {
        log.info("update Quality2 voice Record start ========");
        qualityCheckingVoiceService.updateQualityVoiceRecord(2);
        log.info("update quality2 voice record end ========");
    }

//    @Scheduled(cron = "0 */1 * * * ?")
//    public void updateNotByApi() throws Exception {
//        log.info("update updateNotByApi voice Record start ========");
//        qualityCheckingVoiceService.updateNotByApi();
//        log.info("update updateNotByApi voice record end ========");
//    }
}
