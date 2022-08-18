/*
 * Copyright (c) 2018. utaka and/or its affiliates.
 */

package com.onefly.united.oauth2.captcha.support;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.afs.model.v20180112.AnalyzeNvcRequest;
import com.aliyuncs.afs.model.v20180112.AnalyzeNvcResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.onefly.united.oauth2.captcha.CaptchaProperties;
import com.onefly.united.oauth2.service.ICaptchaProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 阿里巴巴 无痕验证
 */
@Slf4j
@Component
public class AcsAnalyzeNvcProvider implements ICaptchaProvider {
    private static final String VERSION = "1";
    private static final String SCORE_JSON_STRING = JSON.toJSONString(
            ImmutableMap.of(
                    "200", "PASS",
                    "400", "NC",
                    "600", "SC",
                    "800", "BLOCK"
            )
    );
    private static final Set<String> SUCCESS_CODES = Sets.newHashSet("100", "200");

    private final IAcsClient client;

    private final CaptchaProperties.AliYunAcs config;

    @Autowired
    public AcsAnalyzeNvcProvider(CaptchaProperties properties) {
        this.config = properties.getAnalyzeNvc();
        IClientProfile profile = DefaultProfile.getProfile(config.getRegionId(), config.getAccessKey(), config.getSecret());
        this.client = new DefaultAcsClient(profile);
        try {
            DefaultProfile.addEndpoint(config.getRegionId(), config.getRegionId(), config.getProduct(), config.getDomain());
        } catch (ClientException e) {
            log.error("init DefaultAcsClient fail!", e);
            throw new RuntimeException("init DefaultAcsClient fail!", e);
        }
    }

    @Override
    public boolean support(HttpServletRequest request) {
        String version = request.getParameter(ICaptchaProvider.PARAMETER_NAME);
        String nvcVal = request.getParameter("a");
        String callback = request.getParameter("callback");

        return !Strings.isNullOrEmpty(version)
                && !Strings.isNullOrEmpty(nvcVal)
                && !Strings.isNullOrEmpty(callback)
                && VERSION.equalsIgnoreCase(Strings.nullToEmpty(version))
                ;
    }

    @Override
    public void generate(HttpServletRequest request, HttpServletResponse response) throws IOException {
    }

    @Override
    public boolean check(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nvcVal = request.getParameter("a");
        String callback = request.getParameter("callback");

        AnalyzeNvcRequest req = new AnalyzeNvcRequest();
        req.setData(nvcVal);
        req.setScoreJsonStr(SCORE_JSON_STRING);
        Map<String, Object> map = new HashMap(15);
        try {
            AnalyzeNvcResponse acsResponse = client.getAcsResponse(req);
            String bizCode = acsResponse.getBizCode();
            //成功代码：100，200
            if (SUCCESS_CODES.contains(bizCode)) {
                return true;
            } else {
                return false;
            }
        } catch (ClientException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }
}
