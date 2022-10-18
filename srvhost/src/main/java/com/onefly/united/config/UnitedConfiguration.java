package com.onefly.united.config;

import com.onefly.united.common.utils.Result;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author rundon
 */

@Slf4j
@Configuration
@RestControllerAdvice
@EnableOAuth2Client
class UnitedConfiguration extends ResponseEntityExceptionHandler {

    @Value("${error.detail:false}")
    private boolean withCause = false;

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception e,
            @Nullable Object body,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        Throwable cause = e;
        if (e instanceof FeignException) {
            cause = e.getCause();
        }

        log.error("united exception uncache in their ", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(new Result().error(cause.getMessage()));
    }


}
