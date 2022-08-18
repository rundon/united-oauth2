/*
 * Copyright (c) 2018. utaka and/or its affiliates.
 */

package com.onefly.united.oauth2;

import com.alibaba.fastjson.JSON;
import com.onefly.united.oauth2.dao.SimpleClientDetailDao;
import com.onefly.united.oauth2.domain.SimpleClientDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;

/**
 * @author
 */
@Slf4j
@Configuration
@ComponentScan(basePackageClasses = ClientConfiguration.class)
public class ClientConfiguration {

    private final SimpleClientDetailDao clientDetailRepository;

    @Autowired
    public ClientConfiguration(SimpleClientDetailDao clientDetailRepository) {
        this.clientDetailRepository = clientDetailRepository;
    }

    @Bean("clientMyDetailsService")
    public ClientDetailsService clientDetailsService() {
        return clientId -> clientDetailRepository.findByClientId(clientId)
                .map(this::map)
                .orElseThrow(() -> new NoSuchClientException(clientId));
    }

    private ClientDetails map(SimpleClientDetails rs) {
        BaseClientDetails details = new BaseClientDetails(rs.getClientId(), rs.getResourceIds(), rs.getScopes(),
                rs.getAuthorizedGrantTypes(), rs.getAuthorities(), rs.getWebServerRedirectUri());
        details.setClientSecret(rs.getClientSecret());
        details.setAccessTokenValiditySeconds(rs.getAccessTokenValidity());
        details.setRefreshTokenValiditySeconds(rs.getRefreshTokenValidity());
        String json = rs.getAdditionalInformation();
        if (json != null) {
            try {
                Map<String, Object> additionalInformation = JSON.parseObject(json, Map.class);
                details.setAdditionalInformation(additionalInformation);
            } catch (Exception e) {
                log.warn("Could not decode JSON for additional information: " + details, e);
            }
        }
        if (rs.isAutoApprove()) {
            details.setAutoApproveScopes(StringUtils.commaDelimitedListToSet(rs.getScopes()));
        }
        return details;
    }

}
