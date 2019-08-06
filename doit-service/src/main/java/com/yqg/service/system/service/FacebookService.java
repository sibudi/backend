package com.yqg.service.system.service;

import com.alibaba.fastjson.JSONObject;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.enums.user.CertificationEnum;
import com.yqg.common.enums.user.CertificationResultEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.models.BaseRequest;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.HttpTools;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.service.system.request.FacebookRequest;
import com.yqg.service.system.response.FacebookResponse;
import com.yqg.service.third.operators.response.GojekSendSmsResponse;
import com.yqg.service.user.model.UserCertificationInfoInRedis;
import com.yqg.service.user.service.UsrService;
import com.yqg.user.dao.UsrCertificationInfoDao;
import com.yqg.user.entity.UsrCertificationInfo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Didit Dwianto on 2018/1/23.
 */
@Service
@Slf4j
public class FacebookService {

    int connectTimeout = 30000;
    int readTimeout = 10000;

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private UsrCertificationInfoDao usrCertificationInfoDao;
    @Autowired
    private UsrService usrService;

    @Value("${jxl.faceBookCallbackUrl}")
    private String faceBookCallbackUrl;
    @Value("${jxl.sendReportTokenUrl}")
    private String sendTokenUrl;

    @Autowired
    private OkHttpClient httpClient;

    // ??facebook ??url
    public FacebookResponse getFacebookUrl(FacebookRequest facebookRequest, String url) throws ServiceException{

        FacebookResponse response = new FacebookResponse();

        JSONObject requestObj = new JSONObject();
        requestObj.put("website","facebook");
        requestObj.put("name",this.usrService.getUserNameByUuid(facebookRequest.getUserUuid()));
        requestObj.put("client_url",faceBookCallbackUrl+"?userUuid="+facebookRequest.getUserUuid()+"&orderNo="+facebookRequest.getOrderNo());
        String result = HttpTools.post(url, null, JSONObject.toJSON(requestObj).toString(), connectTimeout, readTimeout);
        log.info(result);

        if (null != result){
            JSONObject res = JSONObject.parseObject(result);
            String code = res.get("code").toString();
            if (code.equals("20000")) {
                Map<String,String> resultData = (Map<String, String>) res.get("data");
                response.setReport_task_token(resultData.get("report_task_token"));
                response.setUrl(resultData.get("url"));
            }else{
                //  ??????
                log.error("???????");
                throw new ServiceException(ExceptionEnum.USER_PASSWORD_OR_ACCOUNT_ERROR);
            }
        }
        return response;
    }

    // ?????? facebook ??
    public void getFacebookCallback(Map<String, String> map) {

        String userUuid = map.get("userUuid");
        log.info(userUuid);

        String orderNo = map.get("orderNo");
        log.info(orderNo);

        String reportToken = map.get("report_task_token");
        log.info(reportToken);

        // ??????? ????
        sendReportToken(reportToken);

        UserCertificationInfoInRedis cerInfoRedis = new UserCertificationInfoInRedis();
        cerInfoRedis.setCertType("facebook");
        cerInfoRedis.setUserUuid(userUuid);
        cerInfoRedis.setOrderNo(orderNo);
        cerInfoRedis.setReport_task_token(reportToken);

//            TODO:  ???? ???
        UsrCertificationInfo certificationInfo = new UsrCertificationInfo();
        certificationInfo.setUserUuid(userUuid);
        certificationInfo.setCertificationType(CertificationEnum.FACEBOOK_IDENTITY.getType());

        List<UsrCertificationInfo> scanList =  this.usrCertificationInfoDao.scan(certificationInfo);
        if (CollectionUtils.isEmpty(scanList)){

            certificationInfo.setCertificationData(JSONObject.toJSON(cerInfoRedis).toString());
            certificationInfo.setRemark(reportToken);
            certificationInfo.setCertificationResult(CertificationResultEnum.AUTH_SUCCESS.getType());
            this.usrCertificationInfoDao.insert(certificationInfo);
        }else  {

            UsrCertificationInfo update = scanList.get(0);
            update.setCertificationData(JSONObject.toJSON(cerInfoRedis).toString());
            certificationInfo.setCertificationResult(CertificationResultEnum.AUTH_SUCCESS.getType());
            update.setRemark(reportToken);
            this.usrCertificationInfoDao.update(update);
        }
        // ???redis ???task??????batchId ??????
        this.redisClient.listAdd(RedisContants.USER_CERTIFICATION_LIST ,cerInfoRedis);
    }


    /**
     *  ??facebook??? ???????????
     * */
    public void sendReportToken(String token) {

        log.info("???token?"+token);
        try {



            Request request = new Request.Builder()
                    .url(sendTokenUrl+token)
                    .build();

            Response response = httpClient.newCall(request).execute();
            log.info("???response?"+response);

            if(response.isSuccessful())
            {
                String  responseStr = response.body().string();
                log.info("???????:{}", JsonUtils.serialize(responseStr));
            }else {
                log.info("????");
            }

        }catch (Exception e){
            log.error("??facebook????");
            e.getStackTrace();
        }
    }
}
