package com.onefly.united.oauth2.captcha.support;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.afs.model.v20180112.AuthenticateSigRequest;
import com.aliyuncs.afs.model.v20180112.AuthenticateSigResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.google.common.base.Strings;
import com.onefly.united.oauth2.captcha.CaptchaProperties;
import com.onefly.united.oauth2.service.ICaptchaProvider;
import com.onefly.united.oauth2.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 阿里巴巴 滑动验证码
 */
@Slf4j
@Component
public class AcsAuthenticateSigProvider implements ICaptchaProvider {
    private static final String VERSION = "2";

    private final CaptchaProperties.AliYunAcs config;

    private final IAcsClient client;

    @Autowired
    public AcsAuthenticateSigProvider(CaptchaProperties properties) {
        this.config = properties.getAuthentionteSig();
        IClientProfile profile = DefaultProfile.getProfile(config.getRegionId(), config.getAccessKey(), config.getSecret());
        client = new DefaultAcsClient(profile);
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
        String sid = request.getParameter("csessionid");
//        String sig = request.getParameter("sig");
//        String token = request.getParameter("token");
        String scene = request.getParameter("scene");

        return !Strings.isNullOrEmpty(sid)
//                && !Strings.isNullOrEmpty(sig)
//                && !Strings.isNullOrEmpty(token)
                && !Strings.isNullOrEmpty(scene)
                && VERSION.equalsIgnoreCase(Strings.nullToEmpty(version))
                ;
    }

    @Override
    public void generate(HttpServletRequest request, HttpServletResponse response) throws IOException {
    }

    @Override
    public boolean check(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sid = request.getParameter("csessionid");
        String scene = request.getParameter("scene");
//        String sig = request.getParameter("sig");
//        String token = request.getParameter("token");


        try {
            AuthenticateSigRequest req = new AuthenticateSigRequest();
            req.setAppKey(config.getAppKey());
            req.setSessionId(sid);
            req.setScene(scene);
            req.setRemoteIp(WebUtils.getClientIpAddress(request));
//            req.setToken(token);
//            req.setSig(sig);
            AuthenticateSigResponse rsp = client.getAcsResponse(req);
            return rsp.getCode() == 100;
        } catch (Exception e) {
            log.error("check captcha fail", e);
            return false;
        }

    }
}
