/*
 * Copyright (c) 2018. utaka and/or its affiliates.
 */

package com.onefly.united.oauth2.captcha;

import com.google.common.collect.Lists;
import com.onefly.united.oauth2.captcha.support.AcsAnalyzeNvcProvider;
import com.onefly.united.oauth2.captcha.support.AcsAuthenticateSigProvider;
import com.onefly.united.oauth2.captcha.support.SimpleCaptchaProvider;
import com.onefly.united.oauth2.service.ICaptchaProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 验证码
 */
@Component
public class CaptchaProviderManager {
    private List<ICaptchaProvider> prividers;

    @Value("${captcha.version:0}")
    private int version;

    @Autowired
    public CaptchaProviderManager(
            SimpleCaptchaProvider simpleCaptchaService,
            AcsAnalyzeNvcProvider acsAnalyzeNvcProvider,
            AcsAuthenticateSigProvider alibabaCaptchaProvider) {
        this.prividers = Lists.newArrayList(
                simpleCaptchaService,
                acsAnalyzeNvcProvider,
                alibabaCaptchaProvider
        );
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public ICaptchaProvider getProvider() {
        return prividers.get(version);
    }

    public void generate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String version = request.getParameter(ICaptchaProvider.PARAMETER_NAME);
        if (StringUtils.isNumeric(version)) {
            int index = Integer.parseInt(version);
            if (index >= 0 && index < prividers.size()) {
                prividers.get(index).generate(request, response);
            }
        } else {
            getProvider().generate(request, response);
        }
    }

    public boolean check(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String version = request.getParameter(ICaptchaProvider.PARAMETER_NAME);
        if (StringUtils.isNumeric(version)) {
            int index = Integer.parseInt(version);
            if (index >= 0 && index < prividers.size()) {
                return prividers.get(index).check(request, response);
            } else {
                return false;
            }
        } else {
            return getProvider().check(request, response);
        }
    }
}
