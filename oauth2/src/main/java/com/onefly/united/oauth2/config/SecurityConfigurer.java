package com.onefly.united.oauth2.config;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.onefly.united.common.constant.LogMessageDto;
import com.onefly.united.common.constant.LoginOperationEnum;
import com.onefly.united.common.constant.LoginStatusEnum;
import com.onefly.united.common.redis.RedisMqUtil;
import com.onefly.united.common.user.UserDetail;
import com.onefly.united.common.utils.IpUtils;
import com.onefly.united.common.utils.Result;
import com.onefly.united.oauth2.captcha.CaptchaFilter;
import com.onefly.united.oauth2.captcha.CaptchaProperties;
import com.onefly.united.oauth2.captcha.CaptchaProviderManager;
import com.onefly.united.oauth2.common.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Value("${united.security.password.secret:united}")
    private String secret;

    @Value("${captcha.path:/login/captcha,/api/oauth2/login/captcha}")
    private String[] captcha;

    @Value("${security.ignored:/*.ico,/static/**}")
    private String[] ignored;

    @Value("${security.csrf.cookie.http-only:false}")
    private boolean csrfCookieHttpOnly = false;

    @Autowired
    private CaptchaProperties captchaProperties;

    @Autowired
    private CaptchaProviderManager providerManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(ignored);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/actuator/**", "/login/**", "/register/**").permitAll()
                .anyRequest().authenticated()

                .and()
                .headers().frameOptions().disable()

                .and()
                .csrf().disable()
                .csrf().ignoringAntMatchers("/**/captcha")
                .csrfTokenRepository(csrfCookieHttpOnly ? new CookieCsrfTokenRepository() : CookieCsrfTokenRepository.withHttpOnlyFalse())

                .and()
                .headers().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                //异常处理
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
//                .authenticationEntryPoint(authenticationEntryPoint())

                //注销
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/oauth/logout", "GET"))
                .logoutSuccessHandler(logoutSuccessHandler(tokenStore, redisTemplate));

        if (captchaProperties.isEnabled()) {
            //添加验证码
            http.addFilterBefore(verifyFilter(), UsernamePasswordAuthenticationFilter.class);
        }
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        log.info("Defining UserDetailsServiceAdapter");
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    public Filter verifyFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = Lists.newArrayList();
        filters.add(captchaVerifyFilter());
        filter.setFilters(filters);
        return filter;
    }

    private LogoutSuccessHandler logoutSuccessHandler(TokenStore tokenStore, StringRedisTemplate redisTemplate) {
        return new SsoLogoutSuccessHandler(tokenStore, redisTemplate);
    }

    /**
     * 处理退出登录的逻辑
     */
    private static class SsoLogoutSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler implements LogoutSuccessHandler {

        private TokenStore tokenStore;

        private StringRedisTemplate redisTemplate;

        public SsoLogoutSuccessHandler(TokenStore tokenStore, StringRedisTemplate redisTemplate) {
            this.tokenStore = tokenStore;
            this.redisTemplate = redisTemplate;
        }

        @Override
        public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            String accessToken = extractHeaderToken(request);
            saveLoginLog(request, authentication);
            if (StringUtils.isNotBlank(accessToken)) {
                OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(accessToken);
                if (oAuth2AccessToken != null) {
                    tokenStore.removeAccessToken(oAuth2AccessToken);
                    OAuth2RefreshToken oAuth2RefreshToken = oAuth2AccessToken.getRefreshToken();
                    tokenStore.removeRefreshToken(oAuth2RefreshToken);
                    tokenStore.removeAccessTokenUsingRefreshToken(oAuth2RefreshToken);
                }
            }
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().append(JSON.toJSONString(new Result()));
        }

        private String extractHeaderToken(HttpServletRequest request) {
            Enumeration headers = request.getHeaders("Authorization");
            String value;
            do {
                if (!headers.hasMoreElements()) {
                    return null;
                }
                value = (String) headers.nextElement();
            } while (!value.toLowerCase().startsWith("Bearer".toLowerCase()));
            String authHeaderValue = value.substring("Bearer".length()).trim();
            request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE, value.substring(0, "Bearer".length()).trim());
            int commaIndex = authHeaderValue.indexOf(44);
            if (commaIndex > 0) {
                authHeaderValue = authHeaderValue.substring(0, commaIndex);
            }
            if (StringUtils.isBlank(authHeaderValue)) {
                authHeaderValue = request.getParameter("access_token");
            }
            return authHeaderValue;
        }

        private void saveLoginLog(HttpServletRequest request, Authentication authentication) {
            //用户信息
            UserDetail user = (UserDetail) authentication.getPrincipal();
            LogMessageDto log = new LogMessageDto();
            log.setLogType("01");
            Map<String, Object> data = Maps.newHashMap();
            data.put("ip", IpUtils.getIpAddr(request));
            data.put("userAgent", request.getHeader(HttpHeaders.USER_AGENT));
            data.put("operation", LoginOperationEnum.LOGOUT.value());
            data.put("status", LoginStatusEnum.SUCCESS.value());
            if (user != null) {
                data.put("creator", user.getId());
                data.put("creatorName", user.getUsername());
            }
            data.put("createDate", new Date());
            log.setData(data);
            RedisMqUtil.addQueueTask(JSON.toJSONString(log));
        }
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return (request, response, exception) -> {
            ErrorCode.sendError(response, HttpServletResponse.SC_FORBIDDEN, HttpServletResponse.SC_FORBIDDEN, exception.getMessage());
        };
    }

    /**
     * 添加验证码校验
     */
    private Filter captchaVerifyFilter() {
        OrRequestMatcher matcher = new OrRequestMatcher(RequestMatchers.antMatchers(captcha));
        return new CaptchaFilter(matcher, providerManager);
    }


    private static final class RequestMatchers {

        /**
         * Create a {@link List} of {@link AntPathRequestMatcher} instances.
         *
         * @param httpMethod  the {@link HttpMethod} to use or {@code null} for any
         *                    {@link HttpMethod}.
         * @param antPatterns the ant patterns to create {@link AntPathRequestMatcher}
         *                    from
         * @return a {@link List} of {@link AntPathRequestMatcher} instances
         */
        public static List<RequestMatcher> antMatchers(HttpMethod httpMethod,
                                                       String... antPatterns) {
            String method = httpMethod == null ? null : httpMethod.toString();
            List<RequestMatcher> matchers = new ArrayList<RequestMatcher>();
            for (String pattern : antPatterns) {
                matchers.add(new AntPathRequestMatcher(pattern, method));
            }
            return matchers;
        }

        /**
         * Create a {@link List} of {@link AntPathRequestMatcher} instances that do not
         * specify an {@link HttpMethod}.
         *
         * @param antPatterns the ant patterns to create {@link AntPathRequestMatcher}
         *                    from
         * @return a {@link List} of {@link AntPathRequestMatcher} instances
         */
        public static List<RequestMatcher> antMatchers(String... antPatterns) {
            return antMatchers(null, antPatterns);
        }

        /**
         * Create a {@link List} of {@link RegexRequestMatcher} instances.
         *
         * @param httpMethod    the {@link HttpMethod} to use or {@code null} for any
         *                      {@link HttpMethod}.
         * @param regexPatterns the regular expressions to create
         *                      {@link RegexRequestMatcher} from
         * @return a {@link List} of {@link RegexRequestMatcher} instances
         */
        public static List<RequestMatcher> regexMatchers(HttpMethod httpMethod,
                                                         String... regexPatterns) {
            String method = httpMethod == null ? null : httpMethod.toString();
            List<RequestMatcher> matchers = new ArrayList<RequestMatcher>();
            for (String pattern : regexPatterns) {
                matchers.add(new RegexRequestMatcher(pattern, method));
            }
            return matchers;
        }

        /**
         * Create a {@link List} of {@link RegexRequestMatcher} instances that do not
         * specify an {@link HttpMethod}.
         *
         * @param regexPatterns the regular expressions to create
         *                      {@link RegexRequestMatcher} from
         * @return a {@link List} of {@link RegexRequestMatcher} instances
         */
        public static List<RequestMatcher> regexMatchers(String... regexPatterns) {
            return regexMatchers(null, regexPatterns);
        }

        private RequestMatchers() {
        }
    }
}
