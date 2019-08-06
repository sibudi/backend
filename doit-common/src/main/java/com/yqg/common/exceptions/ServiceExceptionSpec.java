/*
 * Copyright (c) 2014-2015 XXX, Inc. All Rights Reserved.
 */

package com.yqg.common.exceptions;

import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.enums.system.ThirdExceptionEnum;

/**
 * @author Jacob
 *
 */
public class ServiceExceptionSpec extends ErrorCodeException {

    private static final long serialVersionUID = 4881464515416673811L;

    public ServiceExceptionSpec(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getCode(), exceptionEnum.getMessage());
    }

    public ServiceExceptionSpec(ThirdExceptionEnum exceptionEnum) {
        super(exceptionEnum.getCode(), exceptionEnum.getMessage());
    }
}
