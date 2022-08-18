package com.onefly.united.oauth2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.approval.ApprovalStoreUserApprovalHandler;

import java.util.concurrent.TimeUnit;

/**
 * 自定义用户页面
 */
public class MyUserApprovalHandler extends ApprovalStoreUserApprovalHandler {
    private static final String PREFIX = "oauth2:user_approval:";

    @Autowired
    private StringRedisTemplate template;

    @Override
    public boolean isApproved(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
        return super.isApproved(authorizationRequest, userAuthentication)
                && isVerified(userAuthentication);
    }

    public boolean setVerified(Authentication authentication, boolean successful) {
        template.opsForValue().set(confKey(authentication.getName()), String.valueOf(successful), 10, TimeUnit.MINUTES);
        return successful;
    }

    public boolean isVerified(Authentication authentication) {
        return true;
    }

    private String confKey(String key) {
        return PREFIX + key;
    }

}
