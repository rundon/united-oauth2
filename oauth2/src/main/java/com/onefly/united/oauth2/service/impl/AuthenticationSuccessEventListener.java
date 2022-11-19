package com.onefly.united.oauth2.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.onefly.united.common.constant.LogMessageDto;
import com.onefly.united.common.constant.LoginOperationEnum;
import com.onefly.united.common.constant.LoginStatusEnum;
import com.onefly.united.common.redis.RedisMqUtil;
import com.onefly.united.common.user.UserDetail;
import com.onefly.united.common.utils.IpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

@Component
public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private HttpServletRequest request;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        //这里还有oAuth2的客户端认证的事件，需要做一个判断
        if (event.getAuthentication().getDetails().toString().contains("username")) {
            LogMessageDto log = new LogMessageDto();
            log.setLogType("01");
            Map<String, Object> data = Maps.newHashMap();
            UserDetail user = (UserDetail) event.getAuthentication().getPrincipal();
            data.put("ip", IpUtils.getIpAddr(request));
            data.put("userAgent", request.getHeader(HttpHeaders.USER_AGENT));
            data.put("operation", LoginOperationEnum.LOGIN.value());
            data.put("status", LoginStatusEnum.SUCCESS.value());
            data.put("creator", user.getId());
            data.put("creatorName", user.getUsername());
            data.put("createDate", new Date());
            log.setData(data);
            RedisMqUtil.addQueueTask(JSON.toJSONString(log));
        }
    }
}
