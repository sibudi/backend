/*
 * Copyright (c) 2014-2015 XXX, Inc. All Rights Reserved.
 */

package com.yqg.common.exceptions;

/**
 * @author Jacob
 *
 */
public class ErrorCodeException extends Exception {


    private static final long serialVersionUID = -4556515656361708799L;

    private int errorCode = 0;

    public ErrorCodeException(int errorCode, String message) {
        super(message);
        setErrorCode(errorCode);
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

}
