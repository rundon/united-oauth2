/*
 * Copyright (c) 2018. utaka and/or its affiliates.
 */

package com.onefly.united.oauth2.captcha.support;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.google.common.base.Strings;
import com.onefly.united.common.exception.RenException;
import com.onefly.united.oauth2.captcha.CaptchaProperties;
import com.onefly.united.oauth2.captcha.CaptchaStore;
import com.onefly.united.oauth2.service.ICaptchaProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author rundon
 */
@Component
public class SimpleCaptchaProvider implements ICaptchaProvider {

    private final Producer kaptchaProducer;

    private final CaptchaStore store;

    @Autowired
    public SimpleCaptchaProvider(CaptchaProperties properties, CaptchaStore store) {
        DefaultKaptcha producer = new DefaultKaptcha();
        producer.setConfig(new Config(properties.getProperties()));
        this.kaptchaProducer = producer;
        this.store = store;
    }

    @Override
    public boolean support(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session == null) {
            return false;
        }
        String code = (String) session.getAttribute(Constants.KAPTCHA_SESSION_KEY);
        return !Strings.isNullOrEmpty(code);
    }

    // 风险识别
    @Override
    public void generate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader(HttpHeaders.EXPIRES, "0");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, max-age=0, must-revalidate");
        response.setHeader(HttpHeaders.PRAGMA, "no-cache");
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        String captchaText = kaptchaProducer.createText();
        String uuid = request.getParameter("uuid");
        store.save(uuid, captchaText, 300, TimeUnit.SECONDS);
        request.getSession(true).setAttribute(Constants.KAPTCHA_SESSION_KEY, captchaText);
        BufferedImage image = kaptchaProducer.createImage(captchaText);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);

        try {
            out.flush();
        } finally {
            out.close();
        }


    }

    // 风险拦截
    @Override
    public boolean check(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String actual = request.getParameter("captcha_code");
        if (Strings.isNullOrEmpty(actual)) {
            return false;
        }
        String uuid = request.getParameter("uuid");
        if (StringUtils.isBlank(uuid)) {
            return false;
        }

        try {
            Optional<String> code = store.get(uuid);
            if (!code.isPresent()) {
                return false;
            }
            return actual.equals(code.get());
        } finally {
            store.reset(uuid);
        }
    }
}
