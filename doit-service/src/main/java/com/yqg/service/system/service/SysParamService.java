package com.yqg.service.system.service;

import com.yqg.common.redis.RedisClient;
import com.yqg.system.dao.SysParamDao;
import com.yqg.system.entity.SysParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;


/**
 * Created by Didit Dwianto on 2017/11/25.
 */
@Component
@Slf4j
public class SysParamService {

    @Autowired
    private SysParamDao sysParamDao;

    @Autowired
    private RedisClient redisClient;

    public String getSysParamValue(String key) {
        String result = this.redisClient.get(key);
        if (result != null) {
            return result;
        }
        SysParam entity = new SysParam();
        entity.setDisabled(0);
        entity.setSysKey(key);
        List<SysParam> sysParams = this.sysParamDao.scan(entity);
        if (!CollectionUtils.isEmpty(sysParams)) {
            result = sysParams.get(0).getSysValue();
            this.redisClient.set(key, result);
            return result;
        }
        log.info("sys params is empty!");
        return null;

    }


    public void setSysParamValue(String key,String value){

        String result = this.redisClient.get(key);
        if (result != null && result.equals(value)) {
            return;
        }

        this.redisClient.set(key, value);
        this.sysParamDao.updateSysParam(value,key);
    }

    public List<SysParam> getSysParams(){
        SysParam entity = new SysParam();
        entity.setDisabled(0);
        List<SysParam> sysParams = this.sysParamDao.scan(entity);
        if (!CollectionUtils.isEmpty(sysParams)) {
            return sysParams;
        }
        log.info("sys params is empty!");
        return null;
    }
}
