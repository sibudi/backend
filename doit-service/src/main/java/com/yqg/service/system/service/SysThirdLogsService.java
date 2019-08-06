package com.yqg.service.system.service;

import com.yqg.system.dao.SysThirdLogsDao;
import com.yqg.system.entity.SysThirdLogs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Didit Dwianto on 2017/11/26.
 */
@Component
@Slf4j
public class SysThirdLogsService {

    @Autowired
    private SysThirdLogsDao sysThirdLogsDao;

    public void addSysThirdLogs( String orderNo, String userUuid, Integer thirdType,Integer logType, String reqeustStr,String responseStr) {
        SysThirdLogs entity = new SysThirdLogs();
        entity.setUserUuid(userUuid);
        entity.setOrderNo(orderNo);
        entity.setThirdType(thirdType);
        entity.setLogType(logType);
        entity.setRequest(reqeustStr);
        entity.setResponse(responseStr);
        this.sysThirdLogsDao.insert(entity);
    }
}
