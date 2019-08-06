package com.yqg.task;

import com.yqg.service.signcontract.ContractSignService;
import com.yqg.service.task.AsyncTaskService;
import com.yqg.task.entity.AsyncTaskInfoEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
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

    private ExecutorService fixThreadPool = Executors.newFixedThreadPool(10);

    @Scheduled(fixedRate = 60_000)
    public void doDigitalSign() {
        List<AsyncTaskInfoEntity> waitingList = asyncTaskService.getNeedDigitalSignOrders(AsyncTaskInfoEntity.TaskTypeEnum.CONTRACT_SIGN_TASK, 30);
        if (CollectionUtils.isEmpty(waitingList)) {
            log.info("no waitingLing to digital sign.");
            return;
        }
        log.info("the waiting List size of digital sign is: {} ", waitingList.size());
        List<Future<String>> taskResultList = new ArrayList<>();
        for (AsyncTaskInfoEntity entity : waitingList) {
            Future<String> future = fixThreadPool.submit(() ->
                    contractSignService.changeStatusAfterOrderPass(entity));
            taskResultList.add(future);

        }
        for (Future<String> f : taskResultList) {
            try {
                log.info("order: {} sign contract finished... ", f.get());
            } catch (Exception e) {
                log.info("get future result error", e);
            }
        }
        log.info("task finished...");
    }
}
