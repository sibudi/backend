/*
 * Copyright (c) 2014-2015 XXX, Inc. All Rights Reserved.
 */

package com.yqg.common.configurations;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Jacob
 *
 */
@Log4j
@Configuration
public class RedisConfig {

    @Value("${jedis.pool.database}")
    private int database;

    @Value("${jedis.pool.password}")
    private String password;

    @Value("${jedis.pool.timeout}")
    private int timeout;

    @Value("${jedis.pool.config.maxTotal}")
    private int maxTotal;

    @Value("${jedis.pool.config.maxIdle}")
    private int maxIdle;

    @Value("${jedis.pool.config.minIdle}")
    private int minIdle;

    @Value("${jedis.pool.config.maxWaitMillis}")
    private int maxWaitMillis;

    @Value("#{'${jedis.pool.sentinel.nodes}'.split(',')}")
    private String[] nodes;

    @Value("${jedis.pool.sentinel.master}")
    private String masterName;

    @Bean
    public JedisSentinelPool jedisSentinelPool() {

        JedisPoolConfig redisConfig = new JedisPoolConfig();
        redisConfig.setMaxTotal(this.maxTotal);
        redisConfig.setMaxIdle(this.maxIdle);
        redisConfig.setMaxWaitMillis(this.maxWaitMillis);
        redisConfig.setTestOnBorrow(true);
        redisConfig.setTestOnReturn(true);

        Set<String>  nodeSet = new HashSet<>();
        //循环注入至Set中
        for(String node : nodes){
            log.info("Read node:" + node);
            nodeSet.add(node);
        }
        //创建连接池对象
        JedisSentinelPool jedisPool = new JedisSentinelPool(masterName,nodeSet ,redisConfig ,timeout , password, database);
        return jedisPool;
    }

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(this.maxTotal);
        config.setMaxIdle(this.maxIdle);
        config.setMaxWaitMillis(this.maxWaitMillis);
        return new JedisPool(config, "47.74.191.190", 16379, this.timeout, "doit2019",
                1, null);
    }

}

