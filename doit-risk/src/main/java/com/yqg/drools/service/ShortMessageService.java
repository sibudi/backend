package com.yqg.drools.service;

import com.yqg.drools.beans.ShortMessageData;
import com.yqg.drools.utils.JsonUtil;
import com.yqg.mongo.dao.UserMessagesDal;
import com.yqg.mongo.entity.UserMessagesMongo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ShortMessageService {

    @Autowired
    private UserMessagesDal userMessagesDal;

    public List<ShortMessageData> getShortMessageList(String userUuid,String orderNo){
        if(true){
            //业务数据暂定，直接返回不查询
            return new ArrayList<>();
        }
        UserMessagesMongo entity = new UserMessagesMongo();
        if(!StringUtils.isEmpty(userUuid)){
            entity.setUserUuid(userUuid);
        }
        if(!StringUtils.isEmpty(orderNo)){
            entity.setOrderNo(orderNo);
        }
        List<UserMessagesMongo> scanList = this.userMessagesDal.find(entity);
        if (CollectionUtils.isEmpty(scanList)) {
            log.warn("mongodb short message is empty, orderNo: {}, userUuid: {}", orderNo,userUuid);
            return new ArrayList<>();
        }
        List<ShortMessageData> shortMessageList = new ArrayList<>();
        for(UserMessagesMongo mongoData: scanList){
            String data = mongoData.getData();
            if (StringUtils.isEmpty(data)) {
                continue;
            }
            List<ShortMessageData> tmpList = JsonUtil.toList(data, ShortMessageData.class);
            if (CollectionUtils.isEmpty(tmpList)) {
               continue;
            }
            //[{"type":"-1000"}]
            //过滤掉type=-1000 标识未授权
            tmpList = tmpList.stream().filter(e1->!"-1000".equals(e1.getType())).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(tmpList)){
                continue;
            }
            shortMessageList.addAll(tmpList);
        }

        return shortMessageList;
    }

}
