package com.yqg.drools.service;

import com.yqg.drools.beans.UserCallRecordsData;
import com.yqg.drools.utils.JsonUtil;
import com.yqg.mongo.dao.UserCallRecordsDal;
import com.yqg.mongo.entity.UserCallRecordsMongo;
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
public class UserCallRecordService {

    @Autowired
    private UserCallRecordsDal userCallRecordsDal;

    public List<UserCallRecordsData> getUserCallRecordList(String userUuid, String orderNo) {

        if(true){
            //业务数据暂定，直接返回不查询
            return new ArrayList<>();
        }
        // 手机通话记录
        UserCallRecordsMongo search = new UserCallRecordsMongo();
        if (!StringUtils.isEmpty(orderNo)) {
            search.setOrderNo(orderNo);
        }
        if (!StringUtils.isEmpty(userUuid)) {
            search.setUserUuid(userUuid);
        }


        List<UserCallRecordsMongo> orderUserCallRecordsLists = this.userCallRecordsDal.find(search);
        if (CollectionUtils.isEmpty(orderUserCallRecordsLists)) {
            log.error("mongodb data is empty, orderNo: {} ,userUuid: {}", orderNo, userUuid);
            return null;
        }
        List<UserCallRecordsData> userCallRecordsList = new ArrayList<>();
        for (UserCallRecordsMongo userCallRecordsMongo : orderUserCallRecordsLists) {
            String dataStr = userCallRecordsMongo.getData();
            if (StringUtils.isEmpty(dataStr)) {
                continue;
            }
            List<UserCallRecordsData> tmpList = JsonUtil
                    .toList(dataStr, UserCallRecordsData.class);
            if (CollectionUtils.isEmpty(tmpList)) {
                continue;
            }
            tmpList = tmpList.stream().filter(e1 -> !"-1000".equals(e1.getType())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(tmpList)) {
                continue;
            }
            userCallRecordsList.addAll(tmpList);
        }

        return userCallRecordsList;
    }
}
