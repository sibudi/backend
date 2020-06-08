package com.yqg.task;

import com.yqg.service.notification.request.NotificationRequest;
import com.yqg.service.notification.service.SlackNotificationService;
import com.yqg.service.signcontract.ContractSignService;
import com.yqg.service.task.AsyncTaskService;
import com.yqg.task.entity.AsyncTaskInfoEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/****
 * 电子签章任务处理
 */
@Service
@Slf4j
public class DigitalSignTaskScheduler {

    @Autowired
    private AsyncTaskService asyncTaskService;
    @Autowired
    private ContractSignService contractSignService;
    @Autowired
    private SlackNotificationService slackNotificationService;

    private ExecutorService fixThreadPool = Executors.newFixedThreadPool(10);

    //ahalim: speedup for demo
    @Scheduled(cron = "0/30 * * * * ? ")
    //(fixedRate = 60_000)
    public void doDigitalSign() {
        List<AsyncTaskInfoEntity> waitingList = asyncTaskService.getNeedDigitalSignOrders(AsyncTaskInfoEntity.TaskTypeEnum.CONTRACT_SIGN_TASK, 30);
        if (CollectionUtils.isEmpty(waitingList)) {
            log.info("no waiting list to digital sign.");
            return;
        }
        log.info("the waiting List size of digital sign is: {} ", waitingList.size());
        List<Future<String>> taskResultList = new ArrayList<>();
        for (AsyncTaskInfoEntity entity : waitingList) {
            Future<String> future = fixThreadPool.submit(() ->
                    contractSignService.changeStatusAfterOrderPass(entity));
            taskResultList.add(future);

        }
        Map<String, String> errorMap = new HashMap<String, String>();
        for (Future<String> f : taskResultList) {
            try {
               String[] result = f.get().split(";");    //Error is separated by ;
                if (result.length > 1) {
                    errorMap.put(result[0], result[1]);
                }
                log.info("order: {} sign contract finished... ", result[0]);
            } catch (Exception e) {
                log.info("get future result error", e);
                errorMap.put("Unknown", e.toString());
            }
        }
        if (!CollectionUtils.isEmpty(errorMap)) {
            this.sendSlackErrorNotification("DIGISIGN_CREATE_ERROR", new JSONObject(errorMap).toString());
        }
        log.info("task finished...");
    }

    @Scheduled(cron = "0 1 0 1/1 * ?")
    public void doDigitalSignReloadBucket() {
        log.info("Digital Sign reload bucket begin");
        contractSignService.doDigitalSignReloadBucket();
        log.info("Digital Sign reload bucket end");
    }

    private void sendSlackErrorNotification (String subject, String errorMessage){
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setMessage(String.format("*%s* - %s", subject, errorMessage));  
        slackNotificationService.SendNotification(notificationRequest);
    }
}
