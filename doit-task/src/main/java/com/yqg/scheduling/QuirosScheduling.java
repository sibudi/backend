package com.yqg.scheduling;

import com.yqg.service.check.QualityCheckingVoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.yqg.service.third.quiros.QuirosService;

/**
 * Author: tonggen
 * Date: 2019/4/7
 * time: 3:29 PM
 */
@Slf4j
@Component
public class QuirosScheduling {

    @Autowired
    private QuirosService quirosService;

    /****
     * 每隔一分钟 更新语音外呼mp3文件
     *
     */

    @Scheduled(cron = "0 */1 * * * ?")
    public void updateQualityVoiceRecord() throws Exception {
        log.info("update Quiros0 voice Record start ========");
        quirosService.quirosUpdateRecordingUrl();
        log.info("update Quiros0 voice record end ========");
    }

    // //@Scheduled(cron = "0 */1 * * * ?")
    // public void updateQualityVoiceRecord1() throws Exception {
    //     log.info("update Quiros1 voice Record start ========");
    //     qualityCheckingVoiceService.updateQualityVoiceRecord(1);
    //     log.info("update Quiros1 voice record end ========");
    // }

    // //@Scheduled(cron = "0 */1 * * * ?")
    // public void updateQualityVoiceRecord2() throws Exception {
    //     log.info("update Quiros2 voice Record start ========");
    //     qualityCheckingVoiceService.updateQualityVoiceRecord(2);
    //     log.info("update quiros2 voice record end ========");
    // }

//    @Scheduled(cron = "0 */1 * * * ?")
//    public void updateNotByApi() throws Exception {
//        log.info("update updateNotByApi voice Record start ========");
//        qualityCheckingVoiceService.updateNotByApi();
//        log.info("update updateNotByApi voice record end ========");
//    }
}
