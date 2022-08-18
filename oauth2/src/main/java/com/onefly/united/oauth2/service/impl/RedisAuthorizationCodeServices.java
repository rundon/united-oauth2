package com.onefly.united.oauth2.service.impl;

import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.util.IdGenerator;
import org.springframework.util.JdkIdGenerator;

public class RedisAuthorizationCodeServices implements AuthorizationCodeServices {
    private static final String AUTH_CODE = "auth_code:";
    private final ValueOperations<String, OAuth2Authentication> opts;
    private static final IdGenerator idGenerator = new JdkIdGenerator();
    private String prefix = "";

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public RedisAuthorizationCodeServices(ValueOperations<String, OAuth2Authentication> opts) {
        this.opts = opts;
    }

    private String getAuthKey(String key) {
        return prefix + AUTH_CODE + key;
    }


    @Override
    public String createAuthorizationCode(OAuth2Authentication authentication) {
        String code = idGenerator.generateId().toString();
        opts.setIfAbsent(getAuthKey(code), authentication);
        return code;
    }

    @Override
    public OAuth2Authentication consumeAuthorizationCode(String code) throws InvalidGrantException {
        OAuth2Authentication auth = this.remove(code);
        if (auth == null) {
            throw new InvalidGrantException("Invalid authorization code: " + code);
        }
        return auth;
    }

    public OAuth2Authentication remove(String code) {
        OAuth2Authentication authentication = opts.get(getAuthKey(code));
        if (authentication != null) {
            opts.getOperations().delete(code);
        }

        return authentication;
    }


}
