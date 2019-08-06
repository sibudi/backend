package com.yqg.risk.repository;

import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.mongo.dao.OrderBlackTempDal;
import com.yqg.mongo.entity.OrderBlackTempMongo;
import com.yqg.order.entity.OrdBlackTemp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Service
public class OrderBlackTempRepository {
    @Autowired
    private OrderBlackTempDal orderBlackTempDal;

    public void addBlackTempList(List<OrdBlackTemp> blackTempList){
        if(CollectionUtils.isEmpty(blackTempList)){
            return;
        }
        OrderBlackTempMongo insertParam = new OrderBlackTempMongo();
        insertParam.setDisabled(0);
        insertParam.setUuid(UUIDGenerateUtil.uuid());
        insertParam.setOrderNo(blackTempList.get(0).getOrderNo());
        insertParam.setUserUuid(blackTempList.get(0).getUserUuid());
        insertParam.setUpdateTime(new Date());
        insertParam.setCreateTime(new Date());
        insertParam.setRuleResult(JsonUtils.serialize(blackTempList));
    }
}
