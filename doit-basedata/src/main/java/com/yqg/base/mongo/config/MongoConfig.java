/*
 * Copyright (c) 2014-2015 XXX, Inc. All Rights Reserved.
 */

package com.yqg.base.mongo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jacob
 *
 */
@Configuration
public class MongoConfig {
    @Value("${mongo.repleSet}")
    private String repleSet;

    @Value("${mongo.dbName}")
    private String dbName;

    @Value("${mongo.username}")
    private String username;

    @Value("${mongo.password}")
    private String password;

    @Value("${mongo.connectionsPerHost}")
    private int connectionsPerHost;

    @Value("${mongo.threadsAllowedToBlockForConnectionMultiplier}")
    private int threadsAllowedToBlockForConnectionMultiplier;

    protected String getRepleSet() {
        return this.repleSet;
    }

    protected void setRepleSet(String repleSet) {
        this.repleSet = repleSet;
    }

    protected String getUsername() {
        return username;
    }

    protected void setUsername(String username) {
        this.username = username;
    }

    protected String getPassword() {
        return password;
    }

    protected void setPassword(String password) {
        this.password = password;
    }

    protected String getDbName() {
        return this.dbName;
    }

    protected void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public int getConnectionsPerHost() {
        return connectionsPerHost;
    }

    public void setConnectionsPerHost(int connectionsPerHost) {
        this.connectionsPerHost = connectionsPerHost;
    }

    public int getThreadsAllowedToBlockForConnectionMultiplier() {
        return threadsAllowedToBlockForConnectionMultiplier;
    }

    public void setThreadsAllowedToBlockForConnectionMultiplier(int threadsAllowedToBlockForConnectionMultiplier) {
        this.threadsAllowedToBlockForConnectionMultiplier = threadsAllowedToBlockForConnectionMultiplier;
    }
}
