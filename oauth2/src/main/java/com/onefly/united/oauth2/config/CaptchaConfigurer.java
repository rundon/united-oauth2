package com.onefly.united.oauth2.config;


import com.google.common.collect.ImmutableMap;
import com.onefly.united.oauth2.captcha.CaptchaProperties;
import com.onefly.united.oauth2.captcha.CaptchaProviderManager;
import com.onefly.united.oauth2.common.ErrorCode;
import com.onefly.united.oauth2.service.ICaptchaProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class CaptchaConfigurer extends AuthorizationServerConfigurerAdapter {

    private final CaptchaProperties captchaProperties;

    private final CaptchaProviderManager providerManager;

    @Autowired
    public CaptchaConfigurer(
            CaptchaProperties captchaProperties,
            CaptchaProviderManager providerManager) {
        this.captchaProperties = captchaProperties;
        this.providerManager = providerManager;
    }

    private HandlerInterceptor captchaVerifyInterceptor() {
        RequestMatcher matcher = new OAuth2ServerConfigurer.TokenRequestMatcher("/oauth/token", HttpMethod.POST.name());
        return new CaptchaHandlerInterceptor(matcher, providerManager);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        if (captchaProperties.isEnabled()) {
            endpoints.addInterceptor(this.captchaVerifyInterceptor());
        }
    }

    static class CaptchaHandlerInterceptor implements HandlerInterceptor {
        private final RequestMatcher requiresMatcher;

        private final CaptchaProviderManager providerManager;

        public CaptchaHandlerInterceptor(RequestMatcher rm, CaptchaProviderManager cp) {
            this.requiresMatcher = rm;
            this.providerManager = cp;
        }

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                throws Exception {

            if (!requiresMatcher.matches(request)) {
                return true;
            }

            if (HttpMethod.GET.name().equalsIgnoreCase(request.getMethod())) {
                request.setAttribute("_captcha", ImmutableMap.of(
                        "version", providerManager.getVersion(),
                        "parameterName", ICaptchaProvider.PARAMETER_NAME)
                );
                return true;

            }

            if (providerManager.check(request, response)) {
                return true;
            } else {
                ErrorCode.sendError(response, HttpServletResponse.SC_OK, 10007, ErrorCode.ILLIGAL_CAPTCHA_CODE);
                return false;
            }

        }
    }
}
