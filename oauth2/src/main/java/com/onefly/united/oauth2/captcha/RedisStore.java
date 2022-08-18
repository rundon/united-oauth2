package com.onefly.united.oauth2.captcha;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class RedisStore implements CaptchaStore {
    private static final String PREFIX = "conf:captcha:";

    private final StringRedisTemplate template;

    public RedisStore(StringRedisTemplate stringRedisTemplate) {
        this.template = stringRedisTemplate;
    }

    @Override
    public void save(String key, String code, long timeout, TimeUnit unit) {
        template.opsForValue().set(confKey(key), code, timeout, unit);
    }

    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(template.opsForValue().get(confKey(key)));
    }

    @Override
    public void reset(String key) {
        template.delete(confKey(key));

    }

    @Override
    public long getTimeout(String key) {
        return template.getExpire(confKey(key), TimeUnit.MILLISECONDS);
    }

    private String confKey(String key) {
        return PREFIX + key;
    }

}
