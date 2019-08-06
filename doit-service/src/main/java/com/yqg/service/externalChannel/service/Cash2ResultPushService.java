package com.yqg.service.externalChannel.service;

import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.utils.JsonUtils;
import com.yqg.service.externalChannel.request.Cash2ApiParam;
import com.yqg.service.externalChannel.utils.HttpUtil;
import java.util.concurrent.ExecutorService;

import com.yqg.service.user.service.UsrBankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/16
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Service
@Slf4j
public class Cash2ResultPushService {

    @Autowired
    private ExecutorService executor;

    @Autowired
    private UsrBankService usrBankService;

    public void addTask(String userUuid) {
        executor.submit(() -> {
            try {
                usrBankService.sendCardBinResult(userUuid);
            } catch (ServiceException e) {
                log.error("Cash2回调异常",e);
            }
        });
    }
}
