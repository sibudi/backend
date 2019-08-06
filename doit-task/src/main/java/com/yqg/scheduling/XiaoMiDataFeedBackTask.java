package com.yqg.scheduling;

import com.yqg.service.third.xiaomi.XiaoMiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class XiaoMiDataFeedBackTask {

    @Autowired
    private XiaoMiService xiaoMiService;
//    /**
//     * @throws Exception
//     */
//    @Scheduled(cron = "5 3 0 * * ?")
//    public void sendSmsToSilenceUser(){
//        log.info("send xiaomi data begin");
//        xiaoMiService.getUserAndOrderDayDataToMi();
//        log.info("send xiaomi data end");
//    }
}
