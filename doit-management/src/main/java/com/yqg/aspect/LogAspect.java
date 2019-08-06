package com.yqg.aspect;

import com.google.common.collect.ImmutableList;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author alan
 */
@Aspect
@Component
public class LogAspect {

    private final static Logger logger = LoggerFactory.getLogger(LogAspect.class);


    private static List<String> NOLOGURILIST = ImmutableList.of("getOrderUserDataMongo","getOrderUserDataSql","getTwilioMP3"
    ,"getUserFeedBackList","orderUserDataMongo","orderUserDataSql","twilioMP3"
            ,"userFeedBackList","sendSmsBatch","getSendSmsBatch");

    @Pointcut("execution(public * com.yqg.manage.controller..*(..))")
    public void login() {
    }

    @Before("login()")
    public void doBefore(JoinPoint joinPoint) throws Exception {
        if (joinPoint.getArgs() != null) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

            String requestUri = request.getRequestURI();
            if (requestUri.contains("forceChangePassword")
                    || requestUri.contains("twilio")
                    || requestUri.contains("getXmlFile")
                    || requestUri.contains("getTwilioWaResponses")
                    || requestUri.contains("twilioWhatsAppResXML")
                    || requestUri.contains("tempUpdateOrderRole")
                    || requestUri.contains("silentUserXML")
                    || requestUri.contains("upgradeLimitXML")
                    || requestUri.contains("reduceLimitXML")
                    || requestUri.contains("twilioMP3")
                    || requestUri.contains("getExcelFile")
                    || requestUri.contains("showStreamOnBrowser")
                    || requestUri.contains("addUserToBlackList")
                    || requestUri.contains("sendSmsBatch")
                    || requestUri.contains("addBlackListByFile")) {
                return;
            }
            StringBuffer strBuffer = new StringBuffer();
            for (Object obj : joinPoint.getArgs()) {
                String param = null;
                try {
                    if (obj instanceof HttpServletRequest || obj instanceof HttpServletResponse) {
                        continue;
                    }
                    param = JsonUtils.serialize(obj);
                } catch (Exception e) {

                }
                if (StringUtils.isNotBlank(param)) {
                    strBuffer.append(param + "|");
                }
            }
            logger.info("url: {}|request param: {}", requestUri,
                strBuffer.toString());
        }

    }

    @AfterReturning(returning = "object", pointcut = "login()")
    public void doAfterReturning(JoinPoint joinPoint, Object object) {
        if (!NOLOGURILIST.contains(joinPoint.getSignature().getName())) {
            logger.info("method: {} |response: {}", joinPoint.getSignature().getName(),
                    object==null?"":  JsonUtils.serialize(object));
        }
    }
}
