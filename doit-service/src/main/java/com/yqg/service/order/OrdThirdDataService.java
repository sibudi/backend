package com.yqg.service.order;

import com.yqg.common.enums.system.ThirdDataTypeEnum;
import com.yqg.mongo.dao.OrderThirdDataDal;
import com.yqg.mongo.entity.OrderThirdDataMongo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;


import java.util.Date;
import java.util.List;

/**
 * Created by Didit Dwianto on 2017/11/26.
 */
@Component
@Slf4j
public class OrdThirdDataService {

      @Autowired
      private OrderThirdDataDal orderThirdDataDal;

    /**
     */
    public void add(String data, String orderNo, String userUuid, ThirdDataTypeEnum type, int status) {

        try {
            Date date = new Date();
            OrderThirdDataMongo orderThirdDate = new OrderThirdDataMongo();
            orderThirdDate.setCreateTime(date);
            orderThirdDate.setUpdateTime(date);
            orderThirdDate.setUserUuid(userUuid);
            orderThirdDate.setOrderNo(orderNo);
            orderThirdDate.setThirdType(type.getType());
            orderThirdDate.setData(data);
            orderThirdDate.setStatus(status);
            this.orderThirdDataDal.insert(orderThirdDate);

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /**
     */
    public void update(OrderThirdDataMongo updateEntity,String data) {
        updateEntity.setUpdateTime(new Date());
        updateEntity.setData(data);
        this.orderThirdDataDal.updateById(updateEntity);
    }


    /**
     */
    public OrderThirdDataMongo getThridDataByUserUuid(String userUuid, ThirdDataTypeEnum type) {
        OrderThirdDataMongo orderThirdDate = new OrderThirdDataMongo();
        orderThirdDate.setUserUuid(userUuid);
        orderThirdDate.setThirdType(type.getType());
        orderThirdDate.setStatus(1);
        List<OrderThirdDataMongo> orderThirdDatas = this.orderThirdDataDal.find(orderThirdDate);
        if (CollectionUtils.isEmpty(orderThirdDatas)) {
            return null;
        }
        return orderThirdDatas.get(orderThirdDatas.size() - 1);
    }

    /**
     */
    public OrderThirdDataMongo getThridDataByOrderNo(String orderNo, ThirdDataTypeEnum type) {
        OrderThirdDataMongo orderThirdDate = new OrderThirdDataMongo();
        orderThirdDate.setOrderNo(orderNo);
        orderThirdDate.setThirdType(type.getType());
        orderThirdDate.setStatus(1);
        List<OrderThirdDataMongo> orderThirdDatas = this.orderThirdDataDal.find(orderThirdDate);
        if (CollectionUtils.isEmpty(orderThirdDatas)) {
            return null;
        }
        return orderThirdDatas.get(orderThirdDatas.size() - 1);

    }
}
