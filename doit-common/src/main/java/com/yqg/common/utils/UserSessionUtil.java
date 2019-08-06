package com.yqg.common.utils;

import com.google.common.collect.ImmutableList;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.BadRequestException;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.models.BaseRequest;
import com.yqg.common.models.LoginOrderUserSession;
import com.yqg.common.models.LoginSession;
import com.yqg.common.redis.RedisClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * @Author  Jacob on 17/4/15.
 */
@Slf4j
public class UserSessionUtil {

    private static List<String> list = ImmutableList.of("/system/isUpdate","/system/appH5UrlValueList");



    private static final String SESSION_PREFIX = "session:yishu";

    private static final int EXPIRES_SECOND = 30 * 60 * 60;

    public static void filter(HttpServletRequest httpRequest, RedisClient redisClient,
            BaseRequest baseRequest) throws ServiceException {
        Map<String, String> params = new HashMap<>();
        params.put("timestamp", baseRequest.getTimestamp());
        params.put("sessionId", baseRequest.getSessionId());

//        String version=baseRequest.getClient_version();
//        String clientType=baseRequest.getClient_type();
//        boolean tempFlag=true;
//        if(clientType.equals("android")){
//            if(version.compareTo("1.0.7")<0){
//                tempFlag=false;
//            }
//        }
//        if(tempFlag){
//            if (baseRequest.getSign() == null || baseRequest.getTimestamp() == null) {
//                throw new ServiceException(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
//            }
//            if (!SignUtils.signVerify(baseRequest.getSign(), params)) {
//                throw new ServiceException(ExceptionEnum.SESSION_UN_LOGIN);
//            }
//        }

        if (!isContains(httpRequest.getRequestURI())) {
            validationSession(httpRequest, redisClient, baseRequest);
        }

    }

    private static void validationSession(HttpServletRequest request, RedisClient redisClient,
            BaseRequest baseRequest) throws ServiceException {
        String loginSessionStr = getLoginSessionStr(redisClient, baseRequest.getSessionId());
        if (loginSessionStr == null) {
            log.error("???");
            throw new ServiceException(ExceptionEnum.SESSION_UN_LOGIN);
        }
        LoginSession loginSession = JsonUtils.deserialize(loginSessionStr, LoginSession.class);
        if (loginSession.getUserUuid().equals("0")) {
            log.error("???");
            throw new ServiceException(ExceptionEnum.SESSION_UN_LOGIN);
        }
        baseRequest.setUserUuid(loginSession.getUserUuid());
    }

    public static LoginSession getLoginSession(RedisClient redisClient,
        BaseRequest baseRequest) {
        String loginSessionStr = getLoginSessionStr(redisClient, baseRequest.getSessionId());
        if (loginSessionStr == null) {
            return null;
        }
        LoginSession loginSession = JsonUtils.deserialize(loginSessionStr, LoginSession.class);
        if (loginSession.getUserUuid().equals("0")) {
            return null;
        }
        return loginSession;
    }


    private static boolean isContains(String url) {
        if (list.contains(url)) {
            return true;
        }
        return false;
    }

    /**
     * ??????user????sessioId
     *
     */
    public static void generateAndSetSessionId(RedisClient redisClient,
            LoginSession loginSession) throws ServiceException {
        String sessionId = UUID.randomUUID().toString().replaceAll("-", "");
        loginSession.setSessionId(sessionId);
        redisClient.set(SESSION_PREFIX + sessionId, JsonUtils.serialize(loginSession),EXPIRES_SECOND);
    }



    /**
     * ??LoginSession ??json str
     *
     */
    public static String getLoginSessionStr(RedisClient redisClient, String sessionId) {
        return redisClient.get(SESSION_PREFIX + sessionId);
    }

    public static boolean delLoginSession(RedisClient redisClient, String sessionId) {
        Long delKeys = redisClient.del(SESSION_PREFIX + sessionId);
        return delKeys == null ? false : (delKeys.intValue() > 0);
    }
    /**
     * h5??????user????sessioId
     *
     */
    public static void generateAndSetSessionIdForH5(RedisClient redisClient,
                                               LoginSession loginSession) throws BadRequestException {
        String sessionId = UUID.randomUUID().toString().replaceAll("-", "");
        loginSession.setSessionId(sessionId);
        redisClient.set(SESSION_PREFIX + sessionId, JsonUtils.serialize(loginSession),3*60*60);
    }

    /**
     * ?????userUuid?orderNo session
     *
     */
    public static void generateUserOrderSessionId(RedisClient redisClient,String userUuid,String orderNo) throws BadRequestException {
        LoginOrderUserSession loginOrderUserSession=new LoginOrderUserSession(userUuid,orderNo);
        redisClient.set(RedisContants.SESSION_H5_USER_ORDER_PREFIX + userUuid, JsonUtils.serialize(loginOrderUserSession),24 * 60 * 60);
    }

    /**
     * ??userUuid????
     * @param redisClient
     * @param userUuid
     * @return
     */
    public static LoginOrderUserSession getGenerateUserOrderSessionId(RedisClient redisClient,String userUuid) throws ServiceException {
        String s = redisClient.get(RedisContants.SESSION_H5_USER_ORDER_PREFIX + userUuid);
        if(s==null){
            log.error("???????h5??");
            throw new ServiceException(ExceptionEnum.USER_H5_USER_ORDER_IS_NULL);
        }
        return JsonUtils.deserialize(s,LoginOrderUserSession.class);
    }

}
