/*
 * Copyright (c) 2014-2015 XXX, Inc. All Rights Reserved.
 */

package com.yqg.common.exceptions.handler;

import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import org.apache.http.HttpStatus;
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.ImmutableList;
import com.yqg.common.exceptions.ErrorCodeException;
import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.builders.ResponseEntityBuilder;
import com.yqg.common.utils.Base64Utils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.RSAUtils;

/**
 * @author Jacob
 *
 */
@Component
public class ExceptionHandler implements HandlerExceptionResolver {

    private static Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    private static final ImmutableList<?> omittedExceptions = ImmutableList
            .of(ServiceException.class);

    private static final ImmutableList<?> omittedExceptionSpecs = ImmutableList
            .of(ServiceExceptionSpec.class);

    public static void setResponse(HttpServletRequest request, HttpServletResponse response,
            Object obj,
            Exception ex) {

        if (MDC.get("X-Request-Id") == null) {
            MDC.put("X-Request-Id", UUID.randomUUID());
        }

        logger.error("the exception url: ", request!=null?request.getRequestURI():null);

        Throwable t = ex;
        Integer status = HttpStatus.SC_INTERNAL_SERVER_ERROR;
        response.setContentType(MediaType.APPLICATION_JSON);

        if (shouldLogException(ex)) {
            status = HttpStatus.SC_OK;
            logger.error("service exception {}", ex.getMessage());
        }
        if (shouldLogExceptionSpec(ex)) {
            status = HttpStatus.SC_OK;
            response.setStatus(status);
            logger.error("service exception {}", ex.getMessage());
            addExceptionToResponseBody(response, t, null, false);
            return;
        }
        if (status == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            logger.error("Exception {}", ex);
        }
        response.setStatus(status);
        //TODO ????????
        addExceptionToResponseBody(response, t, null, true);

        MDC.remove("X-Request-Id");

    }

    public static void addExceptionToResponseBody(HttpServletResponse response,
            Throwable ex,
            Map<String, String> data,
            boolean isEncode) {
        try {
            ResponseEntity<Object> entity = null;
            if (response.getStatus() == HttpStatus.SC_OK) {
                ErrorCodeException errorCodeException = (ErrorCodeException) ex;
                entity = new ResponseEntityBuilder<Object>()
                        .code(errorCodeException.getErrorCode())
                        .message(errorCodeException.getMessage())
                        .data(data)
                        .build();
            } else {
                entity = new ResponseEntityBuilder<Object>()
                        .code(-1)
                        .message(ex.getMessage())
                        .data(data)
                        .build();
            }
            if (isEncode) {
                byte[] encodedata = RSAUtils.encryptByPublicKey(
                        JsonUtils.serialize(entity).getBytes(),
                        RSAUtils.publicKeyStr);
                String json = Base64Utils.encode(encodedata);
                response.getWriter().print(json);
            } else {
                response.getWriter().print(JsonUtils.serialize(entity));
            }
        } catch (Exception e) {
            logger.error("failed to write response JSON", e);
            MDC.remove("X-Request-Id");
            throw new IllegalStateException(e);
        }
    }

    private static boolean shouldLogException(Exception ex) {
        // only log exceptions that we don't expect
        boolean ret = omittedExceptions.contains(ex.getClass());
        return ret;
    }

    private static boolean shouldLogExceptionSpec(Exception ex) {
        boolean ret = omittedExceptionSpecs.contains(ex.getClass());
        return ret;
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
            Object obj, Exception ex) {
        setResponse(request, response, obj, ex);
        return new ModelAndView();
    }

}