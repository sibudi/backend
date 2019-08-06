/*
 * Copyright (c) 2014-2015 XXX, Inc. All Rights Reserved.
 */

package com.yqg.common.configurations;

import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JettyConfig {

    private static final Logger logger = LoggerFactory.getLogger(JettyConfig.class);

//    @Value("${org.eclipse.jetty.webapp.WebAppContext.maxFormContentSize}")
//    private String maxFormContentSize;

    @Bean
    public JettyEmbeddedServletContainerFactory jettyEmbeddedServletContainerFactory()
            throws Exception {
        JettyEmbeddedServletContainerFactory jettyContainer = new JettyEmbeddedServletContainerFactory();
        jettyContainer.addServerCustomizers(new JettyServerCustomizer() {
            @Override
            public void customize(final Server server) {
                // logger.info("settings maxFormSize to " + JettyConfig.this.maxFormContentSize);
                server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize",
                        -1);
            }
        });
        return jettyContainer;
    }

}
