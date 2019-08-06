package com.yqg.scheduling;

import com.yqg.common.exceptions.ServiceException;
import com.yqg.service.scheduling.KaBinCheckSchedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Didit Dwianto on 2017/11/27.
 */
@Component
@Slf4j
public class KaBinCheckSchedTask {

    @Autowired
    private KaBinCheckSchedService kaBinCheckService;


    @Scheduled(cron = "0 0/1 * * * ?")
    public void kaBinCheck() throws ServiceException {
        log.info("KaBinCheck begin");
        kaBinCheckService.kaBinCheck();
        log.info("KaBinCheck end");
    }

    // 邀请好友活动卡片校验
    @Scheduled(cron = "0 0/1 * * * ?")
    public void activityKaBinCheck() throws ServiceException {
        log.info("activityKaBinCheck begin");
        kaBinCheckService.activityKaBinCheck();
        log.info("activityKaBinCheck end");
    }


}
