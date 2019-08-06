package com.yqg.common.interceptor;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/*****
 * @Author Jeremy Lawrence
 * Created at 2018/1/30
 *
 *
 ****/

public class RequestIdGeneratorInterceptor extends HandlerInterceptorAdapter {

    public static final String X_REQUEST_ID = "X-Request-Id";

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {
        MDC.put(X_REQUEST_ID, UUID.randomUUID().toString());
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
        ModelAndView modelAndView) throws Exception {
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
        Object handler, Exception ex) throws Exception {
        MDC.remove(X_REQUEST_ID);
    }
}
