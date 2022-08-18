package com.onefly.united.oauth2.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onefly.united.oauth2.captcha.CaptchaStore;
import com.onefly.united.oauth2.captcha.RedisStore;
import com.onefly.united.oauth2.service.impl.MyRedisTokenStore;
import com.onefly.united.oauth2.service.impl.RedisAuthorizationCodeServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;

import java.io.IOException;

@Slf4j
@Configuration
public class RedisConfigurer {
    private final String redisPrefix;

    RedisConfigurer(@Value("${spring.redis.prefix:oauth2:}") String redisPrefix) {
        this.redisPrefix = redisPrefix;
    }

    @Bean
    public RedisTemplate<String, OAuth2Authentication> oAuth2AuthenticationRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, OAuth2Authentication> template = new RedisTemplate<String, OAuth2Authentication>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        log.info("###Create RedisTemplate({}) instance success", redisConnectionFactory.toString());
        return template;
    }

    @Bean
    public TokenStore tokenStore(RedisConnectionFactory redisConnectionFactory, ObjectMapper mapper) {
        MyRedisTokenStore tokenStore = new MyRedisTokenStore(redisConnectionFactory);
        tokenStore.setPrefix(redisPrefix);
        tokenStore.setSerializationStrategy(new JacksonSerializationStrategy(mapper));
        log.info("###Create RedisTokenStore({}) instance success", redisPrefix);
        return tokenStore;
    }

    @Bean
    public CaptchaStore captchaStore(StringRedisTemplate stringRedisTemplate) {
        return new RedisStore(stringRedisTemplate);
    }

    @Bean
    public AuthorizationCodeServices authorizationCodeServices(RedisTemplate<String, OAuth2Authentication> redisTemplate) {
        RedisAuthorizationCodeServices services = new RedisAuthorizationCodeServices(redisTemplate.opsForValue());
        services.setPrefix(redisPrefix);
        log.info("###Create RedisAuthorizationCodeServices({}) instance success", redisPrefix);
        return services;
    }

    @Slf4j
    static class JacksonSerializationStrategy extends JdkSerializationStrategy {
        private final ObjectMapper objectMapper;

        JacksonSerializationStrategy(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }


        @Override
        protected <T> T deserializeInternal(byte[] bytes, Class<T> clazz) {
            if (OAuth2AccessToken.class.isAssignableFrom(clazz)) {
                try {
                    return objectMapper.readValue(bytes, clazz);
                } catch (IOException e) {
                    log.error("Jackson deserializeInternal fail", e);
                    return super.deserializeInternal(bytes, clazz);
                }
            } else {
                return super.deserializeInternal(bytes, clazz);
            }
        }

        @Override
        protected byte[] serializeInternal(Object object) {
            if (object instanceof OAuth2AccessToken) {
                try {
                    return objectMapper.writeValueAsBytes(object);
                } catch (JsonProcessingException e) {
                    log.error("Jackson serializeInternal fail", e);
                    return super.serializeInternal(object);
                }
            } else {
                return super.serializeInternal(object);
            }
        }
    }
}
