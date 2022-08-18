package com.onefly.united.oauth2.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName("oauth_client_details")
public class SimpleClientDetails implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     *客户端唯一标识
     */
    private String clientId;
    /**
     *资源ID
     */
    private String resourceIds;
    /**
     *密钥
     */
    private String clientSecret;
    /**
     *原始密钥
     */
    private String clientSecretOriginal;
    /**
     *授权范围
     */
    private String scopes;
    /**
     *授权类型
     */
    private String authorizedGrantTypes;
    /**
     *返回地址
     */
    private String webServerRedirectUri;
    /**
     *授权
     */
    private String authorities;
    /**
     *生成的access_token有效时长(秒)
     */
    private Integer accessTokenValidity = 60 * 10;
    /**
     *生成的refresh_token有效时长(秒)
     */
    private Integer refreshTokenValidity = 60 * 24;
    /**
     *扩展信息
     */
    private String additionalInformation;
    /**
     *自动确认
     */
    private boolean autoApprove = true;
    /**
     *绑定IP地址
     */
    private String bindIpAddress;
    /**
     *访问状态(NONE|BAN)
     */
    private Status status = Status.NONE;
    /**
     *API Key备注名
     */
    private String remark;

    public Set<String> getRegisteredRedirectUri() {
        return StringUtils.commaDelimitedListToSet(this.webServerRedirectUri);
    }

    public void setAdditionalInformation(Map<String, String> additionalInformation) {
        this.additionalInformation = JSON.toJSONString(additionalInformation);
    }


    public Map<String, String> getAdditionalInformationMap() {
        String json = this.getAdditionalInformation();
        if (json != null) {
            try {
                return JSONObject.parseObject(json,Map.class);
            } catch (Exception e) {

            }
        }
        return Maps.newHashMap();
    }

    public enum Status {
        /**
         * 访问正常
         */
        NONE,

        /**
         * 禁止访问
         */
        DENY;

        public boolean equals(Status other) {
            return this.name().equalsIgnoreCase(other.name());
        }
    }
}
