package com.yqg.aspect;

import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.utils.AesUtil;
import com.yqg.common.utils.GetIpAddressUtil;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.service.externalChannel.config.Cash2Config;
import com.yqg.service.externalChannel.config.CheetahConfig;
import com.yqg.service.externalChannel.enums.ExternalChannelEnum;
import com.yqg.service.externalChannel.request.Cash2ApiParam;
import com.yqg.service.externalChannel.response.Cash2Response;
import com.yqg.service.externalChannel.response.CheetahResponse;
import com.yqg.service.externalChannel.service.ExternalChannelDataService;
import com.yqg.service.externalChannel.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/6
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Aspect
@Component
@Slf4j
public class ExternalApiAspect {

    @Autowired
    private ExternalChannelDataService externalChannelDataService;
    @Autowired
    private Cash2Config cash2Config;
    @Autowired
    private CheetahConfig cheetahConfig;


    @Pointcut("@within(com.yqg.common.annotations.CashCashRequest)")
    public void cash2Api() {
    }

    @Around("cash2Api()")
    public Cash2Response invoke(ProceedingJoinPoint pjp) {
        long start = System.currentTimeMillis();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes();
        HttpServletRequest httpRequest = attributes.getRequest();

        Cash2ApiParam request = null;
        Object args[] = pjp.getArgs();
        if (args == null) {
            responseError(Cash2ResponseCode.PARAM_EMPTY_1001, "param is empty");
            return null;
        } else if (!(args[0] instanceof Cash2ApiParam)) {
            responseError(Cash2ResponseCode.PARAM_TYPE_ERROR_1002, null);
            return null;
        } else {
            request = (Cash2ApiParam) args[0];
        }

        //参数解密校验
        log.info("url: {} ", httpRequest.getRequestURI(),
            "not log ...");
        String cipherText = request.getData().getEncryptedData();
        //解密后数据
        String decryptedText = AesUtil
            .decryptData(cipherText, cash2Config.getAesKey(), cash2Config.getInitVector());
        if (StringUtils.isEmpty(decryptedText)) {
            log.error("解密出错, cipherText: {}", cipherText);
            responseError(Cash2ResponseCode.DATA_DECRYPTED_ERROR_1009, null);
            return null;
        }

        //save data to mongo
        saveRequestDataToMongo(decryptedText,ExternalChannelEnum.CASHCASH.name());

        //转为map
        Map<String, Object> paramMap = JsonUtils.toMap(decryptedText);

        if (paramMap == null) {
            responseError(Cash2ResponseCode.PARAM_JSON_FORMAT_ERROR_1003, null);
            return null;
        }
        String sign = (String) paramMap.remove("sign");
        Integer timestamp = (int) paramMap.remove("timestamp");



        String reSign = SecurityUtil.cash2DataSignature(paramMap, timestamp,cash2Config);

        if (!reSign.equals(sign)) {
            log.error("签名校验不通过,reSign: {},sign: {}", reSign, sign);
            //检查ip是否正确，ip白名单正确的话可以忽略验签名
            String ip = GetIpAddressUtil.getIpAddr(httpRequest);
            if(StringUtils.isNotEmpty(ip) && cash2Config.getIpWhiteList().contains(ip)){
                //ok
            }else{
                log.info("ip error and sign error,ip: {}",ip);
                responseError(Cash2ResponseCode.DATA_SIGNATURE_ERROR_1009, null);
                return null;
            }

        }

        try {
            Cash2Response result = (Cash2Response) pjp.proceed();
            log.info("url: {} ,the response is: {}, cost: {} ms", httpRequest.getRequestURI(),
                JsonUtils.serialize(result),
                System.currentTimeMillis() - start);
            return result;
        } catch (ServiceException e){
            log.error("invoke error", e);
            responseError(Cash2ResponseCode.PARAM_TYPE_ERROR_1002, e.getMessage());
        }catch (IllegalArgumentException e) {
            log.error("invoke error", e);
            responseError(Cash2ResponseCode.PARAM_TYPE_ERROR_1002, e.getMessage());
        } catch (Throwable e) {
            log.error("invoke error", e);
            responseError(Cash2ResponseCode.SERVER_INTERNAL_ERROR_9001, null);
        }
        return null;

    }

    private void responseError(Cash2ResponseCode responseCode, String message) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes();
        HttpServletResponse httpResponse = attributes.getResponse();
        HttpServletRequest httpRequest = attributes.getRequest();
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setContentType("application/json; charset=utf-8");
        try {
            PrintWriter writer = httpResponse.getWriter();
            Cash2Response resp = Cash2ResponseBuiler.buildResponse(responseCode);
            if (StringUtils.isNotEmpty(message)) {
                resp.withMessage(message);
            }
            writer.append(JsonUtils.serialize(resp));
            writer.flush();
            writer.close();
            log.info("url: {}, external error response: {}", httpRequest.getRequestURI(),
                JsonUtils.serialize(resp));
        } catch (Exception e) {
            log.error("response error info exception", e);
        }
    }

    private void saveRequestDataToMongo(String data,String channel) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes();
        HttpServletRequest httpRequest = attributes.getRequest();
        String queryStr = "";
        if(HttpMethod.GET.name().equalsIgnoreCase(httpRequest.getMethod())){
            queryStr = httpRequest.getQueryString();
        }
        externalChannelDataService
            .saveExternalChannelRequestDataToMongo(httpRequest.getRequestURI()+queryStr, data,channel
                );
    }


    /**
     *    猎豹金融
     * */
    @Pointcut("@within(com.yqg.common.annotations.CheetahRequest)")
    public void cheetahApi() {
    }

    @Around("cheetahApi()")
    public CheetahResponse invokeForCheetah(ProceedingJoinPoint pjp) {
        long start = System.currentTimeMillis();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletRequest httpRequest = attributes.getRequest();

        Object args[] = pjp.getArgs();

        //参数解密校验
        log.info("url: {} ", httpRequest.getRequestURI(),
                "not log ...");


        String body = "";
        if(args!= null&&args.length>0){
             Object bodyData = args[0];
             if(!(bodyData instanceof HttpServletRequest)){
                 body = JsonUtils.serialize(args[0]);
             }
        }
        // 公参中的sign
        String sign = httpRequest.getParameter("sign");
        String timestamp = httpRequest.getParameter("timestamp");
        String accessKey = httpRequest.getParameter("accessKey");
        String requestMethod =  httpRequest.getMethod();
        if(StringUtils.isEmpty(timestamp)){
            log.info("the timestamp is empty");
            responseError(CheetahResponseCode.PARAM_ERROR_1001, "timestamp is empty ");
            return null;
        }

        String signData = null;
        if(HttpMethod.GET.name().equalsIgnoreCase(requestMethod)){
            signData = timestamp;
        }else{
            signData = timestamp+"$"+body;
        }
        // 自己加密得到的sign
        String reSign = SecurityUtil.cheetahEncryptionSign(signData,cheetahConfig);
        if (!reSign.equals(sign)) {
            log.error("签名校验不通过,reSign: {},sign: {}", reSign, sign);
            responseError(CheetahResponseCode.PARAM_ERROR_1001, "Verification failure ");
            return null;
        }


       //  save data to mongo
       saveRequestDataToMongo(body,ExternalChannelEnum.CHEETAH.name());

        //invoke controller, log response
        try {
            CheetahResponse result = (CheetahResponse) pjp.proceed();
            log.info("url: {} ,the response is: {}, cost: {} ms", httpRequest.getRequestURI(),
                    JsonUtils.serialize(result),
                    System.currentTimeMillis() - start);
            return result;
        } catch (ServiceException e){
            log.error("invoke error", e);
            responseError(CheetahResponseCode.PARAM_ERROR_1001, e.getMessage());
        }catch (IllegalArgumentException e) {
            log.error("invoke error", e);
            responseError(CheetahResponseCode.PARAM_ERROR_1001, e.getMessage());
        } catch (Throwable e) {
            log.error("invoke error", e);
            responseError(CheetahResponseCode.SERVER_INTERNAL_ERROR_3001, null);
        }
        return null;

    }

    private void responseError(CheetahResponseCode responseCode, String message) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletResponse httpResponse = attributes.getResponse();
        HttpServletRequest httpRequest = attributes.getRequest();
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setContentType("application/json; charset=utf-8");
        try {
            PrintWriter writer = httpResponse.getWriter();
            CheetahResponse resp = CheetahResponseBuilder.buildResponse(responseCode);
            if (StringUtils.isNotEmpty(message)) {
                resp.withMessage(message);
            }
            writer.append(JsonUtils.serialize(resp));
            writer.flush();
            writer.close();
            log.info("url: {}, external error response: {}", httpRequest.getRequestURI(),
                    JsonUtils.serialize(resp));
        } catch (Exception e) {
            log.error("response error info exception", e);
        }
    }

}
