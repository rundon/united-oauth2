package com.onefly.united.oauth2.config;

import com.onefly.united.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.security.auth.message.AuthException;

@Slf4j
@Configuration
@ControllerAdvice
public class OAuth2Configuration {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Result> handleOrganizationException(AuthException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new Result().error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder() {
            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                //可以自定义校验规则
                return super.matches(rawPassword, encodedPassword);
            }
        };
    }

    @Bean
    public TokenApprovalStore tokenApprovalStore(TokenStore tokenStore) {
        TokenApprovalStore approvalStore = new TokenApprovalStore();
        approvalStore.setTokenStore(tokenStore);
        return approvalStore;
    }

    @Bean
    public MyUserApprovalHandler userApprovalHandler(ClientDetailsService clientMyDetailsService, TokenApprovalStore tokenApprovalStore) {
        MyUserApprovalHandler handler = new MyUserApprovalHandler();
        handler.setApprovalStore(tokenApprovalStore);
        handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientMyDetailsService));
        handler.setClientDetailsService(clientMyDetailsService);
        return handler;
    }

}
