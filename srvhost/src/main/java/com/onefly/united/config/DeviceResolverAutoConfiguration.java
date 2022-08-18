/*
 * Copyright (c) 2018. utaka and/or its affiliates.
 */

package com.onefly.united.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mobile.device.DeviceHandlerMethodArgumentResolver;
import org.springframework.mobile.device.DeviceResolverHandlerInterceptor;
import org.springframework.mobile.device.view.LiteDeviceDelegatingViewResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import java.util.List;

/**
 * @author rundon
 */
@Configuration
@ConditionalOnClass({DeviceResolverHandlerInterceptor.class, DeviceHandlerMethodArgumentResolver.class})
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
class DeviceResolverAutoConfiguration {

    @Configuration
    @ConditionalOnWebApplication
    protected static class DeviceResolverMvcConfiguration implements WebMvcConfigurer {

        @Autowired
        private DeviceResolverHandlerInterceptor deviceResolverHandlerInterceptor;

        @Bean
        @ConditionalOnMissingBean(DeviceResolverHandlerInterceptor.class)
        public DeviceResolverHandlerInterceptor deviceResolverHandlerInterceptor() {
            return new DeviceResolverHandlerInterceptor();
        }

        @Bean
        public DeviceHandlerMethodArgumentResolver deviceHandlerMethodArgumentResolver() {
            return new DeviceHandlerMethodArgumentResolver();
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(this.deviceResolverHandlerInterceptor);
        }

        @Override
        public void addArgumentResolvers(
                List<HandlerMethodArgumentResolver> argumentResolvers) {
            argumentResolvers.add(deviceHandlerMethodArgumentResolver());
        }

        @Bean
        public LiteDeviceDelegatingViewResolver liteDeviceAwareViewResolver(ThymeleafViewResolver delegate) {
            LiteDeviceDelegatingViewResolver resolver = new LiteDeviceDelegatingViewResolver(delegate);
            resolver.setOrder(delegate.getOrder() - 1);
            resolver.setMobileSuffix(".h5");
//            resolver.setMobilePrefix("/");
//            resolver.setTabletPrefix("/");
            return resolver;
        }
    }
}
