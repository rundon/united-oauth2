package com.onefly.united.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.onefly.united.common.exception.ExceptionUtils;
import com.onefly.united.common.utils.Result;
import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Configuration
@RestControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception e,
            @Nullable Object body,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        if (e instanceof FeignException) {
            log.error("call fallback!", e);
        }
        log.error("united exception uncache in their ", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(new Result().error(e.getMessage()));
    }

    @Bean
    public ErrorDecoder errorDecoder(ObjectMapper objectMapper) {
        return new UserErrorDecoder(objectMapper);
    }

    @Slf4j
    public static class UserErrorDecoder implements ErrorDecoder {

        private final ObjectMapper objectMapper;

        public UserErrorDecoder(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public Exception decode(String methodKey, Response response) {
            String error = null;
            try {
                error = StreamUtils.copyToString(response.body().asInputStream(), Charsets.UTF_8);
                Map<String, String> map = objectMapper.readValue(error, Map.class);
                error = map.getOrDefault("message", error).toUpperCase();
            } catch (IOException ex) {
                log.error(ex.getMessage(), ex);
            }
            // 这里只封装4开头的请求异常
            if (500 == response.status()) {
                return new HystrixBadRequestException(error);
            } else {
                log.error(error);
                return null;
            }
        }
    }
}
