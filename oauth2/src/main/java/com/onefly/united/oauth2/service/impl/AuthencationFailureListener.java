package com.onefly.united.oauth2.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.onefly.united.common.constant.Constant;
import com.onefly.united.common.constant.LogMessageDto;
import com.onefly.united.common.constant.LoginOperationEnum;
import com.onefly.united.common.constant.LoginStatusEnum;
import com.onefly.united.common.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.event.*;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
public class AuthencationFailureListener implements ApplicationListener<AbstractAuthenticationFailureEvent> {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void onApplicationEvent(AbstractAuthenticationFailureEvent event) {
        LogMessageDto msg = new LogMessageDto();
        msg.setLogType("01");
        Map<String, Object> data = Maps.newHashMap();
        //提供的凭据是错误的，用户名或者密码错误
        if (event instanceof AuthenticationFailureBadCredentialsEvent) {
            Map user = (LinkedHashMap) event.getAuthentication().getDetails();
            if (user != null) {
                String username = (String) user.get("username");
                if (username.contains(":")) {
                    username = username.substring(0, username.length() - 1);
                }
                data.put("ip", IpUtils.getIpAddr(request));
                data.put("userAgent", request.getHeader(HttpHeaders.USER_AGENT));
                data.put("operation", LoginOperationEnum.LOGIN.value());
                data.put("status", LoginStatusEnum.FAIL.value());
                data.put("creatorName", username);
                data.put("createDate", new Date());
                msg.setData(data);
                redisTemplate.convertAndSend(Constant.LOG_CHANNEL_TOPIC, JSON.toJSONString(msg));
            } else {
                log.error("用户名为空");
            }

        } else if (event instanceof AuthenticationFailureCredentialsExpiredEvent) {
            //验证通过，但是密码过期
            log.error("---AuthenticationFailureCredentialsExpiredEvent---");
        } else if (event instanceof AuthenticationFailureDisabledEvent) {
            //验证过了但是账户被禁用
            log.error("---AuthenticationFailureDisabledEvent---");
        } else if (event instanceof AuthenticationFailureExpiredEvent) {
            //验证通过了，但是账号已经过期
            log.error("---AuthenticationFailureExpiredEvent---");
        } else if (event instanceof AuthenticationFailureLockedEvent) {
            //账户被锁定
            log.error("---AuthenticationFailureLockedEvent---");
        } else if (event instanceof AuthenticationFailureProviderNotFoundEvent) {
            //配置错误，没有合适的AuthenticationProvider来处理登录验证
            log.error("---AuthenticationFailureProviderNotFoundEvent---");
        } else if (event instanceof AuthenticationFailureProxyUntrustedEvent) {
            //代理不受信任，用于Oauth、CAS这类三方验证的情形，多属于配置错误
            log.error("---AuthenticationFailureProxyUntrustedEvent---");
        } else if (event instanceof AuthenticationFailureServiceExceptionEvent) {
            //其他任何在AuthenticationManager中内部发生的异常都会被封装成此类
            log.error("---AuthenticationFailureServiceExceptionEvent---");
        }
    }
}
