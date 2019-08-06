/*
 * Copyright (c) 2014-2015 XXX, Inc. All Rights Reserved.
 */

package com.yqg.common.exceptions;

/**
 * @author Jacob
 *
 */
public class BadRequestException extends ErrorCodeException {

    private static final long serialVersionUID = 4881464515416673811L;

    private static final String SERVER_ERROR_MESSAGE = "Internet server error";

    public BadRequestException() {
        super(500, SERVER_ERROR_MESSAGE);
    }
}
