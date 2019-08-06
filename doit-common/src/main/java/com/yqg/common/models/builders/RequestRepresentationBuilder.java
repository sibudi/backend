/*
 * Copyright (c) 2014-2015 XXX, Inc. All Rights Reserved.
 */

package com.yqg.common.models.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yqg.common.models.RequestRepresentation;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * This class implements a builder for {@link RequestRepresentation} object.
 * @author Jacob
 *
 */
public class RequestRepresentationBuilder {

    private Long timestamp;

    private RequestMethod method;

    private String path;

    private Map<String, List<String>> headers;

    private Map<String, List<String>> parameterMap;

    public RequestRepresentationBuilder timestamp(Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public RequestRepresentationBuilder method(RequestMethod method) {
        this.method = method;
        return this;
    }

    public RequestRepresentationBuilder path(String path) {
        this.path = path;
        return this;
    }

    public RequestRepresentationBuilder headers(Map<String, List<String>> headers) {
        this.headers = headers;
        return this;
    }

    public RequestRepresentationBuilder parameterMap(Map<String, List<String>> parameterMap) {
        this.parameterMap = parameterMap;
        return this;
    }

    public RequestRepresentation build() {
        RequestRepresentation requestRepresentation = new RequestRepresentation();
        requestRepresentation.setTimestamp(this.timestamp);
        requestRepresentation.setMethod(this.method);
        requestRepresentation.setPath(this.path);
        requestRepresentation.setHeaders(this.headers);
        requestRepresentation.setParameterMap(this.parameterMap);
        return requestRepresentation;
    }

    public static RequestRepresentation createTestModel() {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("test.headers.key.1", new ArrayList<>());
        headers.get("test.headers.key.1").add("1.1");
        headers.get("test.headers.key.1").add("1.2");
        headers.put("test.headers.key.2", new ArrayList<>());
        headers.get("test.headers.key.2").add("2.1");
        headers.get("test.headers.key.2").add("2.2");
        Map<String, List<String>> params = new HashMap<>();
        params.put("test.parameterMap.key.1", new ArrayList<>());
        params.get("test.parameterMap.key.1").add("1.1");
        params.get("test.parameterMap.key.1").add("1.2");
        params.put("test.parameterMap.key.2", new ArrayList<>());
        params.get("test.parameterMap.key.2").add("2.1");
        params.get("test.parameterMap.key.2").add("2.2");
        return new RequestRepresentationBuilder()
                .timestamp(1110000L)
                .method(RequestMethod.POST)
                .path("http://localhost:8765/good")
                .headers(headers)
                .parameterMap(params)
                .build();
    }
}
