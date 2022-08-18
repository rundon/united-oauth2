package com.onefly.united.oauth2.config;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

@Configuration
@EnableAuthorizationServer
public class OAuth2ServerConfigurer extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private AuthorizationCodeServices authorizationCodeServices;

    @Autowired
    private ClientDetailsService clientMyDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserApprovalHandler userApprovalHandler;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(this.clientMyDetailsService);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpointsConfigurer) throws Exception {
        endpointsConfigurer
                .userDetailsService(userDetailsService)
                .tokenStore(this.tokenStore)
                .authorizationCodeServices(this.authorizationCodeServices)
                .authenticationManager(this.authenticationManager)
                .userApprovalHandler(this.userApprovalHandler)
                .getFrameworkEndpointHandlerMapping()
                .setMappings(
                        ImmutableMap.of(
                                "/oauth/confirm_access", "/login/confirm"
                        )
                );
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer serverSecurityConfigurer) throws Exception {
        serverSecurityConfigurer
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .allowFormAuthenticationForClients()
        ;
    }

    static class TokenRequestMatcher implements RequestMatcher {

        private final AntPathRequestMatcher baseMatcher;

        TokenRequestMatcher(String pattern, String httpMethod) {
            this.baseMatcher = new AntPathRequestMatcher(pattern, httpMethod);
        }

        @Override
        public boolean matches(HttpServletRequest request) {
            boolean match = baseMatcher.matches(request);
            if (match) {
                String grantType = request.getParameter("grant_type");
                if (!Strings.isNullOrEmpty(grantType) && "password".equalsIgnoreCase(grantType)) {
                    return true;
                } else {
                    return false;
                }
            }
            return match;
        }
    }
}
