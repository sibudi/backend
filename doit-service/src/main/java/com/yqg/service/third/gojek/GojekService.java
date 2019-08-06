package com.yqg.service.third.gojek;

import com.alibaba.fastjson.JSONObject;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.HttpTools;
import com.yqg.service.third.gojek.request.GojekRequest;
import com.yqg.user.dao.UsrCertificationInfoDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Didit Dwianto on 2017/12/20.
 */
@Service
@Slf4j
public class GojekService {

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private UsrCertificationInfoDao usrCertificationInfoDao;

    int connectTimeout = 30000;
    int readTimeout = 10000;


    public JSONObject begin(String url, GojekRequest request)
            throws ServiceException {
        String result = HttpTools.post(url, null, JSONObject.toJSON(request).toString(), connectTimeout, readTimeout);
        log.info(result);
        JSONObject res = JSONObject.parseObject(result);
        return res;
    }

}
