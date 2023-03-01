package com.onefly.united.oauth2.captcha;

import com.onefly.united.oauth2.common.ErrorCode;
import com.onefly.united.oauth2.filter.BaseRequiresMatcherFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 生成验证码
 */
public class CaptchaFilter extends BaseRequiresMatcherFilter {

    private final CaptchaProviderManager captchaManage;

    public CaptchaFilter(RequestMatcher requiresMatcher, CaptchaProviderManager captchaService) {
        super(requiresMatcher);
        this.captchaManage = captchaService;
    }

    @Override
    protected void handleFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            captchaManage.generate(request, response);
        } catch (Exception e) {
            ErrorCode.sendError(response, HttpServletResponse.SC_OK, 10007, ErrorCode.ILLIGAL_CAPTCHA_CODE);
        }
    }
}
