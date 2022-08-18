package com.onefly.united.oauth2.captcha;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * 验证码配置文件
 */
@Data
@Component
@ConfigurationProperties(prefix = "captcha")
public class CaptchaProperties {
    private String[] path = new String[]{"/login", "/register", "/pwd"};

    private boolean enabled = false;

    private Properties properties = new Properties();

    private AliYunAcs analyzeNvc = new AliYunAcs();

    private AliYunAcs AuthentionteSig = new AliYunAcs();

    @Data
    public static class AliYunAcs {
        private String appKey;
        private String regionId = "cn-hangzhou";
        private String domain = "afs.aliyuncs.com";
        private String product = "afs";
        private String accessKey = "LTAIlClsOI7KOBS8";
        private String secret = "bkUWPafvzsWKv9wSb6QkL42gacCiSZ";
    }
}
