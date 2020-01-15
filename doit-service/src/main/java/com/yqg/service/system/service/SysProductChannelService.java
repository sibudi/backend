package com.yqg.service.system.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.redis.RedisClient;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.order.OrdService;
import com.yqg.service.p2p.service.P2PService;
import com.yqg.service.system.request.SysPaymentChannelRequest;
import com.yqg.service.system.response.SysPaymentChannelResponse;
import com.yqg.service.system.response.SysPaymentChannelTypeResponse;
import com.yqg.service.user.service.UsrBankService;
import com.yqg.system.dao.SysPaymentChannelDao;
import com.yqg.system.dao.SysProductChannelDao;
import com.yqg.system.entity.SysPaymentChannel;
import com.yqg.user.entity.UsrBank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by Didit Dwianto on 2017/11/26.
 */
@Component
@Slf4j
public class SysProductChannelService {


    @Autowired
    private SysProductChannelDao sysProductChannelDao;

    /**
     *   ??????
     */
    public String getProductUuid(Integer channel) throws ServiceException {
        log.info("getSysProductChannel with channel "+ channel);

        List<String> productChannels = sysProductChannelDao.getProductChannel(channel);

        if(productChannels.size() == 1){
            return productChannels.get(0);
        }
        
        return "";
    }

    /**
     *   ??????
     */
    public List<String> getProductUuids(Integer channel) throws ServiceException {
        log.info("getSysProductChannel with channel "+ channel);

        List<String> productChannels = sysProductChannelDao.getProductChannel(channel);

        return productChannels;
    }
  

}
