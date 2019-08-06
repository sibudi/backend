package com.yqg.drools.service;

import com.yqg.drools.beans.ContactData;
import com.yqg.drools.utils.JsonUtil;
import com.yqg.mongo.dao.UserContactsDal;
import com.yqg.mongo.entity.UserContactsMongo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserContactService {

    @Autowired
    private UserContactsDal userContactsDal;

    public List<ContactData> getContactList(String userUuid,String orderNo){
        if(true){
            //业务数据暂定，直接返回不查询
            return new ArrayList<>();
        }
        UserContactsMongo search = new UserContactsMongo();
        if(!StringUtils.isEmpty(userUuid)){
            search.setUserUuid(userUuid);
        }

        if(!StringUtils.isEmpty(orderNo)){
            search.setOrderNo(orderNo);
        }

        List<UserContactsMongo> orderUserContacts = this.userContactsDal.find(search);
        if (CollectionUtils.isEmpty(orderUserContacts)) {
            log.warn("mongodb contact data is empty orderNo: {},userUuid: {}", orderNo,userUuid);
            return new ArrayList<>();
        }
        List<ContactData> contactList = new ArrayList<>();
        for(UserContactsMongo userContactsMongo :orderUserContacts){
            String userContactData = userContactsMongo.getData();
            if(StringUtils.isEmpty(userContactData)){
                continue;
            }
            List<ContactData> tmpList = JsonUtil.toList(userContactData, ContactData.class);
            if(CollectionUtils.isEmpty(tmpList)){
                continue;
            }
            contactList.addAll(tmpList);
        }

        return contactList;
    }
}
