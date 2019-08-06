package com.yqg.aspect;

import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.models.BaseRequest;
import com.yqg.common.models.LoginSession;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.common.utils.UserSessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/*****
 * @Author zengxiangcai
 * Created at 2018/1/30
 * @Email zengxiangcai@yishufu.com
 * api 接口拦截：记录出参入参日志，校验是否登录
 ****/

@Aspect
@Component
@Slf4j
public class RestApiLogAspect {

    ThreadLocal<Long> requsetStartTime = new ThreadLocal<>();

    //免记录入参日志url
    private static final List<String> inputLogExcludeUrls = Arrays
            .asList("/upload");

    //免记录返回参数url
    private static final List<String> outputLogExcludeUrls = Arrays.asList("/upload/uploadContacts","/upload/uploadApps","/upload/uploadCallRecords","/users/initSupplementInfo");

    //免登录url
    private static List<String> loginExcludeUrls = Arrays
            .asList("/web/users", "/facebook/getFacebookData", "/operator/getOprator",
                    "/system/smsCode", "/system/isUpdate", "/system/appH5UrlValueList",
                    "/system/isUploadUserApps", "/system/getDicItemListByDicCode", "/users/signup","/users/inviteSignup",
                    "/users/smsAutoLogin");

    @Autowired
    private RedisClient redisClient;


    @Pointcut("(@within(org.springframework.web.bind.annotation.RestController) " +
            "|| @within(org.springframework.stereotype.Controller)) && !@within" +
            "(com.yqg.common.annotations.CashCashRequest)" + "&& !@within" +
            "(com.yqg.common.annotations.CheetahRequest)")
    public void apiController() {
        System.err.println("why...");
    }

    @Before("apiController()")
    public void beforeInvoke(JoinPoint joinPoint) throws Exception {

        requsetStartTime.set(System.currentTimeMillis());

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String requestUri = request.getRequestURI();
        Object args[] = joinPoint.getArgs();

        if (args == null) {
            log.info("url: {}|request param: {}", requestUri, null);
            if (!isUrlExcluded(loginExcludeUrls, requestUri)) {
                log.warn("need toLogin...");
                throw new ServiceException(ExceptionEnum.SESSION_UN_LOGIN);
            }
            return;
        }

        //校验登录
        Optional<Object> param = Arrays.asList(args).stream()
                .filter(elem -> BaseRequest.class.isAssignableFrom(elem.getClass())).findFirst();
        BaseRequest baseRequest = null;
        if (param.isPresent()) {
            baseRequest = (BaseRequest) param.get();
            doAuthorization(requestUri, baseRequest);
        }

        StringBuffer paramJson = new StringBuffer();
        Arrays.asList(args).stream()
                .filter(elem -> elem != null && !(elem instanceof HttpServletRequest)
                        && !(elem instanceof HttpServletResponse))
                .forEach(elem -> {
                    try {
                        paramJson.append(JsonUtils.serialize(elem) + "|");
                    } catch (Exception e) {
                    }
                });

        //入参日志
        if (request.getMethod().equals("GET")) {
            String getParam = request.getQueryString();

            if (isUrlExcluded(inputLogExcludeUrls, requestUri)) {
                log.info("url: {} | request param: {} | orderNo: {} | userUuid: {}", requestUri,
                        "not log...", findGetKeyInfo(getParam, "orderNo"),
                        findGetKeyInfo(getParam, "userUuid"));

                return;
            }
            log.info("url: {} | request param: {}", requestUri, getParam);
        } else {
            if (isUrlExcluded(inputLogExcludeUrls, requestUri)) {
                log.info("url: {} | request param: {} | orderNo: {} | userUuid: {}", requestUri,
                        "not log...", findGetKeyInfo(paramJson.toString(), "orderNo"),
                        findGetKeyInfo(paramJson.toString(), "userUuid"));

                return;
            }
            log.info("url: {} | request param: {}", requestUri,
                    paramJson);
        }

    }

    /***
     * 解析post参数中的orderNo,userUuid
     * @param paramJson
     * @param key
     * @return
     */

    public static String findPostKeyInfo(String paramJson, String key) {
        if(StringUtils.isEmpty(key)){
            return null;
        }
        try {
            int keyIndexStart = paramJson.indexOf(key);
            if (keyIndexStart < 0) {
                return null;
            }
            int keyIndexEnd = paramJson.indexOf("\",", keyIndexStart);

            if (keyIndexEnd < 0) {
                return null;
            }
            return paramJson.substring(keyIndexStart + (key + "\":\"").length(), keyIndexEnd);
        } catch (Exception e) {
            return null;
        }

    }

    /***
     * 解析get参数中的orderNo,userUuid
     * @param param
     * @param key
     * @return
     */
    public static String findGetKeyInfo(String param, String key) {
        if(StringUtils.isEmpty(key)){
            return null;
        }
        try {
            int keyIndexStart = param.indexOf(key);
            if (keyIndexStart < 0) {
                return null;
            }
            int keyIndexEnd = param.indexOf("&", keyIndexStart);

            if (keyIndexEnd < 0) {
                return null;
            }
            return param.substring(keyIndexStart + (key + "=").length(), keyIndexEnd);
        } catch (Exception e) {
            return null;
        }

    }

    public static void main(String[] args) {
        String str = " {\"net_type\":\"4G\",\"system_version\":\"7.0\",\"client_type\":\"android\",\"channel_sn\":\"10001\",\"channel_name\":\"playStore\",\"deviceId\":\"357059081241395\",\"client_version\":\"1.3.1\",\"resolution\":\"1920*1080\",\"IPAdress\":\"10.56.233.242\",\"sign\":\"JkZjhjNWI3YTgzNDUzMTBiZmM1NmVjZTBiNDc4M2\",\"timestamp\":\"1519330915\",\"sessionId\":\"c78293c336304ff2baf41250966506e4\",\"userUuid\":\"72248DBDB7C041BD85ABF0AA702B0240\",\"mac\":null,\"wifimac\":null,\"lbsX\":null,\"lbsY\":null,\"orderNo\":\"011802230253249410\",\"certificationType\":3,\"certificationData\":\"\",\"certificationResult\":null,\"attachmentUrl\":\"/MyUpload/VIDEO/N/N/c577134de38e88e377b8636defe957d3.mp4\",\"ipadress\":\"10.56.233.242\"}";
        System.err.println(findPostKeyInfo(str,"userUuid"));
        System.err.println(findPostKeyInfo(str,"orderNo"));


        String getStr = " userUuid=90791C020F3545EFA4E42A48A6952106&orderNo=011802221746216610&report_task_token=0ce3e1b0ea5b4a3a93c6c3c84192fa56&code=SUCCESS&api_key=43f4df6b145a47dcbd7e5d894104fec3&sdk=false";

        System.err.println(findGetKeyInfo(getStr,"userUuid"));
        System.err.println(findGetKeyInfo(getStr,"orderNo"));

    }

    /***
     * 判断realUrl是否在limitUrls中
     * @param limitUrls
     * @param realUrl
     * @return
     */
    private boolean isUrlExcluded(List<String> limitUrls, String realUrl) {
        if (CollectionUtils.isEmpty(limitUrls)) {
            return false;
        }
        return limitUrls.stream().filter(elem -> realUrl.contains(elem)).findFirst().isPresent();
    }

    private void doAuthorization(String requestUrl,
                                 BaseRequest baseRequest) throws ServiceException {
        if (isUrlExcluded(loginExcludeUrls, requestUrl)) {
            return;
        }
        LoginSession loginSession = UserSessionUtil.getLoginSession(redisClient, baseRequest);
        if (loginSession == null) {
            log.warn("need to login, param: {}", JsonUtils.serialize(baseRequest));
            throw new ServiceException(ExceptionEnum.SESSION_UN_LOGIN);
        }
        baseRequest.setUserUuid(loginSession.getUserUuid());
    }

    @AfterReturning(returning = "retValue", pointcut = "apiController()")
    public void afterInvoke(JoinPoint joinPoint, Object retValue) throws Exception {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String requestUri = request.getRequestURI();

            //返回参数：
            if (isUrlExcluded(outputLogExcludeUrls, requestUri)) {
                log.info("url: {} | response: {} | cost = {} ms", requestUri, "not log...",
                        getRequestCost());
                return;
            }
            log.info("url: {} | response: {} | cost = {} ms", requestUri,
                    retValue == null ? null : JsonUtils.serialize(retValue), getRequestCost());
        } catch (Exception e) {
            log.warn("add response log error..", e);
        }
    }

    private Long getRequestCost() {
        try {
            return System.currentTimeMillis() - requsetStartTime.get();
        } catch (Exception e) {
        }
        return null;
    }

}
