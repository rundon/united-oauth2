package com.onefly.united.oauth2.filter;

import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class BaseRequiresMatcherFilter extends OncePerRequestFilter {
    private final RequestMatcher requiresMatcher;

    protected BaseRequiresMatcherFilter(RequestMatcher requiresMatcher) {
        this.requiresMatcher = requiresMatcher;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!requiresMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        handleFilterInternal(request, response, filterChain);

    }


    /**
     * 过滤其的具体处理方法
     *
     * @param request     要处理的 {@link HttpServletRequest}
     * @param response    要处理的 {@link HttpServletResponse}
     * @param filterChain 过滤链
     * @throws ServletException {@link ServletException}
     * @throws IOException      {@link IOException}
     */
    protected abstract void handleFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException;
}
