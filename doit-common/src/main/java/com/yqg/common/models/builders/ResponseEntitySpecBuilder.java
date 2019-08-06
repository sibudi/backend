/*
 * Copyright (c) 2014-2015 XXX, Inc. All Rights Reserved.
 */

package com.yqg.common.models.builders;

import com.yqg.common.models.ResponseEntitySpec;

/**
 * @author Jacob
 *
 */
public class ResponseEntitySpecBuilder<T> {
    private int code;

    private String message;

    private T data;

    public ResponseEntitySpecBuilder() {
    }

    public ResponseEntitySpecBuilder<T> code(int code) {
        this.code = code;
        return this;
    }

    public ResponseEntitySpecBuilder<T> message(String message) {
        this.message = message;
        return this;
    }

    public ResponseEntitySpecBuilder<T> data(T data) {
        this.data = data;
        return this;
    }

    public ResponseEntitySpec<T> build() {
        ResponseEntitySpec<T> result = new ResponseEntitySpec<>();
        result.setCode(this.code);
        result.setMessage(this.message);
        result.setData(this.data);
        return result;
    }

    public static <T> ResponseEntitySpec<T> success(T data) {
        return new ResponseEntitySpecBuilder<T>().code(1).message("success").data(data).build();
    }

    public static <T> ResponseEntitySpec<T> success() {
        return new ResponseEntitySpecBuilder<T>().code(1).message("success").build();
    }

    public static <T> ResponseEntitySpec<T> successForP2P() {
        return new ResponseEntitySpecBuilder<T>().code(0).message("success").build();
    }
    public static <T> ResponseEntitySpec<T> successForP2P(T data) {
        return new ResponseEntitySpecBuilder<T>().code(0).message("success").data(data).build();
    }

    public static <T> ResponseEntitySpec<T> faildForP2P(T data) {
        return new ResponseEntitySpecBuilder<T>().code(1).message("success").data(data).build();
    }

    public static <T> ResponseEntitySpec<T> faildForP2P(String msg) {
        return new ResponseEntitySpecBuilder<T>().code(1).message(msg).build();
    }

    public static <T> ResponseEntitySpec<T> faildForP2P() {
        return new ResponseEntitySpecBuilder<T>().code(1).message("faild").build();
    }
}
