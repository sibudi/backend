/*
 * Copyright (c) 2014-2015 XXX, Inc. All Rights Reserved.
 */

package com.yqg.common.models.builders;

import com.yqg.common.models.LoginSession;
import com.yqg.common.utils.JsonUtils;

/**
 * @author Jacob
 *
 */
public class LoginSessionBuilder {

    private LoginSession session;

    public LoginSessionBuilder() {
        this.session = new LoginSession();
    }

    public LoginSessionBuilder userUuid(String userUuid) {
        this.session.setUserUuid(userUuid);
        return this;
    }

    public LoginSessionBuilder userName(String userName) {
        this.session.setUserName(userName);
        return this;
    }

    public LoginSessionBuilder mobile(String mobile) {
        this.session.setMobile(mobile);
        return this;
    }

    public LoginSessionBuilder sessionId(String sessionId) {
        this.session.setSessionId(sessionId);
        return this;
    }

    public LoginSessionBuilder loginTime(String loginTime) {
        this.session.setLoginTime(loginTime);
        return this;
    }

    public LoginSessionBuilder lastVerifyTime(String lastVerifyTime) {
        this.session.setLastVerifyTime(lastVerifyTime);
        return this;
    }

    public LoginSessionBuilder clientVersion(String clientVersion) {
        this.session.setClientVersion(clientVersion);
        return this;
    }

    public LoginSessionBuilder appName(String appName) {
        this.session.setAppName(appName);
        return this;
    }

    public LoginSessionBuilder ip(String ip) {
        this.session.setIp(ip);
        return this;
    }

    public LoginSessionBuilder expireIn(String expireIn) {
        this.session.setExpireIn(expireIn);
        return this;
    }

    public LoginSessionBuilder status(String status) {
        this.session.setStatus(status);
        return this;
    }

    public LoginSession build() {
        return this.session;
    }

    public static LoginSession build(String data) {
        return JsonUtils.deserialize(data, LoginSession.class);
    }

    public static LoginSession createTestModel() {
        LoginSessionBuilder builder = new LoginSessionBuilder();
        builder.mobile("12345678900")
                .userName("Jacob")
                .appName("yishu")
                //.userId("1000")
                .clientVersion("2.1.1")
                .expireIn("77760000L")
                .ip("192.168.0.1")
                .lastVerifyTime("1459847365L")
                .loginTime("1459847365L")
                .sessionId("14A59F58-233D-0A55-7E98-B3C61B1C81F3")
                .status("1");

        return builder.build();
    }
    
    public static void main(String[] args) {
        System.err.println(JsonUtils.serialize(createTestModel()));
    }
}
