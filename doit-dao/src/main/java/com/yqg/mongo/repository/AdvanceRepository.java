package com.yqg.mongo.repository;


import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.mongo.dao.AdvanceMongoDataDal;
import com.yqg.mongo.entity.AdvanceMongoData;
import com.yqg.order.entity.OrdOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class AdvanceRepository {
    @Autowired
    private AdvanceMongoDataDal advanceMongoDataDal;

    public void saveData(OrdOrder order, String response, String requestType) {
        AdvanceMongoData data = new AdvanceMongoData();
        data.setOrderNo(order.getUuid());
        data.setUserUuid(order.getUserUuid());
        data.setRequestType(requestType);
        data.setResponseData(response);
        data.setDisabled(0);
        data.setCreateTime(new Date());
        data.setUpdateTime(new Date());
        data.setUuid(UUIDGenerateUtil.uuid());
        advanceMongoDataDal.insert(data);
    }
}
