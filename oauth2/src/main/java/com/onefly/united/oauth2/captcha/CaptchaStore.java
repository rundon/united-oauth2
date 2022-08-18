/*
 * Copyright (c) 2018. utaka and/or its affiliates.
 */

package com.onefly.united.oauth2.captcha;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author rundon
 */
public interface CaptchaStore {

    /**
     * 存储验证码
     *
     * @param key     验证码的唯一标识，必须和验证时候保持一致，比如可以按照手机号，邮箱地址
     * @param code    验证码
     * @param timeout 超时时长
     * @param unit    超时时长单位
     */
    void save(String key, String code, long timeout, TimeUnit unit);

    /**
     * 获取验证码
     *
     * @param key 验证码的唯一标识，必须和验证时候保持一致，比如可以按照手机号，邮箱地址
     * @return 返回验证码信息，如果没有则返回{@code null}
     */
    Optional<String> get(String key);

    /**
     * 重置验证码
     *
     * @param key 验证码的唯一标识，必须和验证时候保持一致，比如可以按照手机号，邮箱地址
     */
    void reset(String key);

    /**
     * 获取验证码的超时时间(毫秒)
     *
     * @param key 验证码的唯一标识，必须和验证时候保持一致，比如可以按照手机号，邮箱地址
     * @return 如果存在则返回，否则返回-1
     */
    long getTimeout(String key);

}
