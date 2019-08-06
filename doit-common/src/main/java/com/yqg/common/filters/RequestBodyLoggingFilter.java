/*
 * Copyright (c) 2014-2016 XXX, Inc. All Rights Reserved.
 */

package com.yqg.common.filters;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.AbstractRequestLoggingFilter;


// To enable this filter, add profile name "verbose_logging" to the active-profiles config of the service, and submit our bitbucket code base.
// To enable this locally on a dev box, add the profile above to "application.properties" file of the target app, e.g. device-service.
// For device service, that config file name is device-service.properties. The actual log text will look like below:
//   After request [uri=/users/uid123/mobileAppActivities;payload={##  "details": "string of jimz test",##  "mobileDeviceId": "imei-abcd"##}]
// Side note: currently we have two logger filters, and they complement each other. LogFilter has headers, while this one provides us request body.
// Later on we might re-factor them according to feedback from PROD real usage.
/**
 * @author Jacob
 *
 */
//@Component
public class RequestBodyLoggingFilter extends AbstractRequestLoggingFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestBodyLoggingFilter.class);

    private static final int LOG_PAYLOAD_MAX_LENGTH = 500;

    public RequestBodyLoggingFilter() {
        super();
        this.setIncludePayload(true);
        this.setMaxPayloadLength(LOG_PAYLOAD_MAX_LENGTH);
    }

    /**
     * Writes a log message before the request is processed.
     */
    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        logger.info(message);
    }

    /**
     * Writes a log message after the request is processed.
     */
    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
    }

}
