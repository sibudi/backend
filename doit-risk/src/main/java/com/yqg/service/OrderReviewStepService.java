package com.yqg.service;

import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.risk.dao.OrderReviewStepDao;
import com.yqg.risk.entity.OrderReviewStepEntity;
import com.yqg.risk.entity.OrderReviewStepEntity.StepEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrderReviewStepService {
    @Autowired
    private OrderReviewStepDao orderReviewStepDao;

    /**
     * get the finished step of order
      * @param orderNo
     * @return
     */
    public OrderReviewStepEntity getCurrentReviewStep(String orderNo) {
        OrderReviewStepEntity dbEntity = orderReviewStepDao.getLatestStep(orderNo);
        return dbEntity;
    }

    public void addReviewStep(String orderNo,String userUuid,StepEnum stepEnum){
        OrderReviewStepEntity insertData = new OrderReviewStepEntity();
        insertData.setOrderNo(orderNo);
        insertData.setUserUuid(userUuid);
        insertData.setStep(stepEnum.getCode());
        insertData.setCreateTime(new Date());
        insertData.setUpdateTime(new Date());
        insertData.setUuid(UUIDGenerateUtil.uuid());
        insertData.setDisabled(0);
        orderReviewStepDao.insert(insertData);
    }
}
