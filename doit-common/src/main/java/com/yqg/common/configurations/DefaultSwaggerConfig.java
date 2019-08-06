/*
 * Copyright (c) 2014-2015 XXX, Inc. All Rights Reserved.
 */

package com.yqg.common.configurations;

import static springfox.documentation.schema.AlternateTypeRules.newRule;

import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicate;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author Jacob
 *
 */
public abstract class DefaultSwaggerConfig {

    private static final String DISABLE_SWAGGER = "DISABLE_SWAGGER";
    private static final String SWAGGER_SHOW_ALL_ENDPOINTS = "SWAGGER_SHOW_ALL_ENDPOINTS";

    private TypeResolver typeResolver = new TypeResolver();

    public abstract String getServiceName();

    @Bean
    public Docket petApi() {
        // Swagger UI to 2.4.0 version: https://github.com/springfox/springfox/issues/1100
        ApiInfo info = new ApiInfoBuilder()
                .title(this.getServiceName() + " service API")
                .license("License")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.")
                .version("0.0.0")
                .build();

        boolean enableSwagger = System.getenv().get(DISABLE_SWAGGER) == null;
        Predicate<RequestHandler> swaggerSelector = System.getenv()
                .get(SWAGGER_SHOW_ALL_ENDPOINTS) == null
                        ? RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)
                        : RequestHandlerSelectors.any();

        return new Docket(DocumentationType.SWAGGER_2).enable(enableSwagger)
                .apiInfo(info).select()
                .apis(swaggerSelector)
                .paths(PathSelectors.any()).build().pathMapping("/")
                .genericModelSubstitutes(ResponseEntity.class)
                .alternateTypeRules(newRule(
                        this.typeResolver.resolve(DeferredResult.class,
                                this.typeResolver.resolve(ResponseEntity.class,
                                        WildcardType.class)),
                        this.typeResolver.resolve(WildcardType.class)));
    }
}
