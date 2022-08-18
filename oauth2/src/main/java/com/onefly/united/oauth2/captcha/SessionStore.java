/*
 * Copyright (c) 2018. utaka and/or its affiliates.
 */

package com.onefly.united.oauth2.captcha;

import com.google.common.base.Strings;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author rundon
 */
public class SessionStore implements CaptchaStore {
    private static final String CAPTCHA_CODE_SESSION_KEY = "_CAPTCHA_CODE_SESSION_KEY";
    private static final String CAPTCHA_CODE_TIMEOUT_SESSION_KEY = "_CAPTCHA_CODE_TIMEOUT_SESSION_KEY";
    private static final String TIMESTAMP_SESSION_KEY = "_TIMESTAMP_CODE_SESSION_KEY";

    @Override
    public void save(String key, String code, long timeout, TimeUnit unit) {
        setAttribute(key, CAPTCHA_CODE_SESSION_KEY, code);
        setAttribute(key, CAPTCHA_CODE_TIMEOUT_SESSION_KEY, unit.toMillis(timeout));
        setAttribute(key, TIMESTAMP_SESSION_KEY, System.currentTimeMillis());
    }

    @Override
    public void reset(String key) {
        removeAttribute(key, CAPTCHA_CODE_SESSION_KEY);
        removeAttribute(key, CAPTCHA_CODE_TIMEOUT_SESSION_KEY);
        removeAttribute(key, TIMESTAMP_SESSION_KEY);

    }

    @Override
    public Optional<String> get(String key) {
        String code = (String) getAttribute(key, CAPTCHA_CODE_SESSION_KEY);
        if (Strings.isNullOrEmpty(code)) {
            return Optional.empty();
        }
        long timeout = (long) getAttribute(key, CAPTCHA_CODE_TIMEOUT_SESSION_KEY);
        long timestamp = (long) getAttribute(key, TIMESTAMP_SESSION_KEY);
        //15分钟
        if (System.currentTimeMillis() - timestamp > timeout) {
            return Optional.empty();
        }
        return Optional.of(code);
    }


    @Override
    public long getTimeout(String key) {
        Object result = getAttribute(key, CAPTCHA_CODE_TIMEOUT_SESSION_KEY);
        return result == null ? -1L : (long) result;
    }

    private Object getAttribute(String key, String attributeName) {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return attributes.getAttribute(attributeName, RequestAttributes.SCOPE_SESSION);
    }

    private void setAttribute(String key, String attributeName, Object value) {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        attributes.setAttribute(attributeName, value, RequestAttributes.SCOPE_SESSION);
    }

    private void removeAttribute(String key, String attributeName) {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        attributes.removeAttribute(attributeName, RequestAttributes.SCOPE_SESSION);
    }
}
