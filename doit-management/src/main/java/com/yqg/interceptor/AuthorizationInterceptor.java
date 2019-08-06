package com.yqg.interceptor;

import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.builders.ResponseEntityBuilder;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.GetIpAddressUtil;
import com.yqg.common.utils.JsonUtils;
import com.yqg.manage.service.user.response.ManSysLoginResponse;
import com.yqg.service.util.LoginSysUserInfoHolder;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Slf4j
public class AuthorizationInterceptor implements HandlerInterceptor {


    @Autowired
    private RedisClient redisClient;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse, Object o) throws Exception {
        MDC.put("X-Request-Id", UUID.randomUUID().toString());
        log.info("the request url is: {}",httpServletRequest.getRequestURI());
        String sessionId = httpServletRequest.getParameter("sessionId");
        if (StringUtils.isEmpty(sessionId)) {
            writeUnLoginException(httpServletResponse);
            return false;
        }

        String userInfo = this.redisClient.get(RedisContants.MANAGE_SESSION_PREFIX + sessionId);
        if (StringUtils.isEmpty(userInfo)) {
            writeUnLoginException(httpServletResponse);
            return false;
        }
        ManSysLoginResponse loginUser = JsonUtils.deserialize(userInfo, ManSysLoginResponse.class);
        LoginSysUserInfoHolder.setLoginSysUserId(loginUser.getId());
        LoginSysUserInfoHolder.setSessionIdUser(sessionId);
        String ipAddr = GetIpAddressUtil.getIpAddr(httpServletRequest);
        String innerIp = httpServletRequest.getParameter("ip");
        log.info("user: {}|operation: {} , user IP is {}, inner ip is {}", JsonUtils.serialize(loginUser),
            httpServletRequest.getRequestURI(), ipAddr, innerIp);
        return true;
    }


    private void writeUnLoginException(HttpServletResponse response) {
        try {
            log.info("need to login...");
            response.setStatus(200);
            ServiceExceptionSpec exception = new ServiceExceptionSpec(
                ExceptionEnum.MANAGE_SESSION_UN_LOGIN);
            ResponseEntity entity = (new ResponseEntityBuilder())
                .code(exception.getErrorCode()).message(exception.getMessage())
                .data(null).build();
            response.getWriter().print(JsonUtils.serialize(entity));
            response.getWriter().close();
        } catch (Exception e) {
            log.error("write un_login Exception error", e);
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView)
        throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        MDC.remove("X-Request-Id");
    }
}
