package com.yqg.infinity;


import com.yqg.common.utils.DateUtils;
import com.yqg.service.third.infinity.InfinityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 语音呼叫定时任务
 */
@Slf4j
@Service
public class InfinityTask {

    @Autowired
    private InfinityService infinityService;

    /****
     * 分机状态
     */
    @Scheduled(cron = "0 0/40 9-18 * * ?")
    public void queryNumberInfo() throws Exception {
        String startTime = DateUtils.DateToString2(new Date());
        log.info("queryNumberInfo----------startTime:" + startTime);
        Map<String, String> map = new HashMap<String, String>();
        map.put("token",infinityService.getToken());
//        map.put("token","0fcdd1af9a259eb28d8c244f64d725a8");
        infinityService.updateExtNumber(map);
        log.info("queryNumberInfo----------endTime:" + DateUtils.DateToString2(new Date()));
    }

    public static void main(String[] args) {
        InfinityTask task = new InfinityTask();
//        task.queryBill();
        try {
            task.queryNumberInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
