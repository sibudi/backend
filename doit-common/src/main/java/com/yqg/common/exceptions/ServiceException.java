/*
 * Copyright (c) 2014-2015 XXX, Inc. All Rights Reserved.
 */

package com.yqg.common.exceptions;

import com.yqg.common.enums.system.ExceptionEnum;

/**
 * @author Jacob
 */
public class ServiceException extends ErrorCodeException {

    private static final long serialVersionUID = 4881464515416673811L;
    private ExceptionEnum exceptionEnum;
    private ServiceExceptionSpec serviceExceptionSpec;

    public ServiceException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getCode(), exceptionEnum.getMessage());
        this.exceptionEnum = exceptionEnum;
    }

    /**
     * ?????
     * @return
     */
    public ServiceExceptionSpec getSpec() {
        if (null == serviceExceptionSpec) {
            serviceExceptionSpec = new ServiceExceptionSpec(this.exceptionEnum);
        }
        return serviceExceptionSpec;
    }
}
