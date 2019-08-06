/*
 * Copyright (c) 2014-2015 XXX, Inc. All Rights Reserved.
 */

package com.yqg.common.models.builders;

import com.yqg.common.models.H5ResponseEntity;
import com.yqg.common.models.ResponseEntity;

/**
 * @author Jacob
 *
 */
public class ResponseEntityBuilder<T> {
    private int code;

    private String message;

    private T data;

    public ResponseEntityBuilder() {
    }

    public ResponseEntityBuilder<T> code(int code) {
        this.code = code;
        return this;
    }

    public ResponseEntityBuilder<T> message(String message) {
        this.message = message;
        return this;
    }

    public ResponseEntityBuilder<T> data(T data) {
        this.data = data;
        return this;
    }

    public ResponseEntity<T> build() {
        ResponseEntity<T> result = new ResponseEntity<>();
        result.setCode(this.code);
        result.setMessage(this.message);
        result.setData(this.data);
        return result;
    }

    public H5ResponseEntity<T> buildH5() {
        H5ResponseEntity<T> result = new H5ResponseEntity<>();
        result.setCode(this.code);
        result.setMessage(this.message);
        result.setData(this.data);
        return result;
    }

    public static <T> ResponseEntity<T> success(T data) {
        return new ResponseEntityBuilder<T>().code(1).message("success").data(data).build();
    }

    public static <T> ResponseEntity<T> success() {
        return new ResponseEntityBuilder<T>().code(1).message("success").build();
    }

    public static <T> H5ResponseEntity<T> successH5(T data) {
        return new ResponseEntityBuilder<T>().code(1).message("success").data(data).buildH5();
    }

    public static <T> H5ResponseEntity<T> successH5() {

        return new ResponseEntityBuilder<T>().code(1).message("success").buildH5();
    }
}
