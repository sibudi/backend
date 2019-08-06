package com.yqg.service.task;

import com.yqg.order.entity.OrdOrder;
import com.yqg.task.dao.AsyncTaskInfoDao;
import com.yqg.task.entity.AsyncTaskInfoEntity;
import com.yqg.task.entity.AsyncTaskInfoEntity.TaskStatusEnum;
import com.yqg.task.entity.AsyncTaskInfoEntity.TaskTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AsyncTaskService {

    @Autowired
    private AsyncTaskInfoDao asyncTaskInfoDao;

    public boolean addTask(OrdOrder order, TaskTypeEnum taskType) {
        AsyncTaskInfoEntity entity = new AsyncTaskInfoEntity();
        entity.setOrderNo(order.getUuid());
        entity.setUserUuid(order.getUserUuid());
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        entity.setTaskStatus(TaskStatusEnum.WAITING.getCode());
        entity.setTaskType(taskType.getCode());
        Integer affectRow = asyncTaskInfoDao.insert(entity);
        return affectRow != null && affectRow == 1;
    }

    public boolean updateTaskStatus(AsyncTaskInfoEntity oldTask, TaskStatusEnum toStatus) {
        log.info("the old status is: {}, and toStatus is: {}", oldTask.getTaskStatus(), toStatus);
        oldTask.setTaskStatus(toStatus.getCode());
        Integer affectRow = asyncTaskInfoDao.update(oldTask);
        return affectRow != null && affectRow == 1;
    }

    public List<AsyncTaskInfoEntity> getNeedDigitalSignOrders(TaskTypeEnum taskType, Integer limitCount) {
        return asyncTaskInfoDao.getNeedDigitalSignOrders(taskType.getCode(), limitCount);
    }


}
