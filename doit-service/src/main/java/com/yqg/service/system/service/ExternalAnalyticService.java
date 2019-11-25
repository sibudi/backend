package com.yqg.service.system.service;

import com.alibaba.fastjson.JSONObject;
import com.yqg.common.models.BaseRequest;
import com.yqg.common.utils.DateUtils;
import com.yqg.order.dao.CouponConfigDao;
import com.yqg.order.entity.CouponConfig;
import com.yqg.order.entity.CouponRecord;
import com.yqg.order.entity.ExternalAnalytic;
import com.yqg.service.system.response.CouponResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yqg.order.dao.CouponRecordDao;
import com.yqg.order.dao.ExternalAnalyticDao;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Janhsen on 2019/11/22.
 */
@Service
@Slf4j
public class ExternalAnalyticService {

    @Autowired
    private ExternalAnalyticDao externalAnalyticDao;

    public void Insert(ExternalAnalytic entity){
        externalAnalyticDao.insert(entity);
    }    
    public void Update(ExternalAnalytic entity){
        externalAnalyticDao.update(entity);
    }
}
