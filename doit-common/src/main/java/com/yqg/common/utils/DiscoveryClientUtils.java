/*
 * Copyright (c) 2014-2015 XXX, Inc. All Rights Reserved.
 */

package com.yqg.common.utils;

import java.net.URI;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Utility class for {@link DiscoveryClient}.
 * @author Jacob
 *
 */
public class DiscoveryClientUtils {

    private static final Logger logger = LoggerFactory.getLogger(DiscoveryClientUtils.class);

    private static final Random rand = new Random();

    public static String getInstanceUri(DiscoveryClient discoveryClient, String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        if (instances == null || instances.isEmpty()) {
            throw new IllegalStateException("No instance found for service " + serviceName);
        }
        ServiceInstance instance = instances.get(rand.nextInt(instances.size()));
        URI uri = instance.getUri();
        String instanceUri = UriComponentsBuilder.fromUri(uri).scheme("https").toUriString();
        logger.info("instanceUri {}", instanceUri);
        return instanceUri;
    }

}
