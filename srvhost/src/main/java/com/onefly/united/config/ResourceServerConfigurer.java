package com.onefly.united.config;

import com.onefly.united.oauth2.common.ErrorCode;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableResourceServer
class ResourceServerConfigurer extends ResourceServerConfigurerAdapter {

    private static final String RESOURCE_ID = "oauth";

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId(RESOURCE_ID).stateless(false);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/api/**")
                .authorizeRequests()
                .antMatchers("/api/message/**", "/api/oauth2/**", "/api/public/**", "/api/resource/**", "/api/groupmanager/**").permitAll()
                .anyRequest().authenticated()

                .and()
                .logout().disable()
                .csrf().disable()
                .headers().frameOptions().disable()

                .and()
                .exceptionHandling()
                .authenticationEntryPoint((request, response, e) -> ErrorCode.sendError(response, HttpServletResponse.SC_UNAUTHORIZED, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage()));

    }
}

