package com.onefly.united.oauth2.utils;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import com.google.common.net.InternetDomainName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * @author rundon
 */
@Slf4j
public class WebUtils extends org.springframework.web.util.WebUtils {
    private static Splitter commaSplitter = Splitter.on(',').trimResults();
    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    private static final String[] HOST = {
            HttpHeaders.X_FORWARDED_HOST,
            HttpHeaders.HOST,
            HttpHeaders.ORIGIN
    };

    private static final String[] LANG = {
            "website"
    };

    /**
     * 默认语言
     */
    private static final String DEFAULT_LANG = "zh";

    /**
     * 从头部提取用户IP
     *
     * @param request
     * @return
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        for (String header : IP_HEADER_CANDIDATES) {
            String ip = request.getHeader(header);
            if (!Strings.isNullOrEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
                return commaSplitter.splitToList(ip).stream().findFirst().orElse(ip);
            }
        }
        return request.getRemoteAddr();
    }

    /**
     * 从头部提取Host
     *
     * @param request
     * @return
     */
    public static String getHost(HttpServletRequest request) {
        for (String header : HOST) {
            // TODO 为了查看测试环境上到底收到的是什么请求而打出全部的日志，上线的话可以改成debug 才打日志
            log.debug("request header {}={}", header, request.getHeader(header));

            String host = request.getHeader(header);
            if (!Strings.isNullOrEmpty(host) && !"unknown".equalsIgnoreCase(host)) {
                Optional<String> validHost = commaSplitter.splitToList(host).stream().findFirst()
                        .filter(InternetDomainName::isValid);
                if (validHost.isPresent()) {
                    return validHost.get();
                }
            }
        }
        return UriComponentsBuilder.fromUriString(request.getRequestURL().toString()).build().getHost();
    }


    public String obtainHost() {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        return getHost(request);
    }

    /**
     * 从头部获取语言
     *
     * @param request
     * @return
     */
    public static String getWebSite(HttpServletRequest request) {
        for (String header : LANG) {
            String lang = request.getHeader(header);
            if (!Strings.isNullOrEmpty(lang) && !"unknown".equalsIgnoreCase(lang)) {
                return lang;
            }
        }
        return DEFAULT_LANG;
    }

}
