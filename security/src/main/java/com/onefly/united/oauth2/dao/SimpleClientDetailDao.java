package com.onefly.united.oauth2.dao;

import com.onefly.united.common.dao.BaseDao;
import com.onefly.united.oauth2.domain.SimpleClientDetails;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

/**
 *
 */
@Mapper
public interface SimpleClientDetailDao extends BaseDao<SimpleClientDetails> {

    /**
     * 根据TOKEN查询客户端信息
     *
     * @param clientId 要查询的 TOKEN 字符串
     * @return 返回客户端 {@link SimpleClientDetails} 信息
     */
    @Select("SELECT " +
            "d.client_id AS clientId," +
            "d.resource_ids AS resourceIds," +
            "d.client_secret AS clientSecret," +
            "d.client_secret_original AS clientSecretOriginal," +
            "d.scopes," +
            "d.authorized_grant_types AS authorizedGrantTypes," +
            "d.web_server_redirect_uri AS webServerRedirectUri," +
            "d.authorities," +
            "d.access_token_validity AS accessTokenValidity," +
            "d.refresh_token_validity AS refreshTokenValidity," +
            "d.additional_information AS additionalInformation," +
            "d.auto_approve AS autoApprove," +
            "d.bind_ip_address AS bindIpAddress," +
            "d.`status`," +
            "d.remark " +
            "FROM " +
            "oauth_client_details d " +
            "WHERE " +
            "d.client_id = #{clientId}")
    Optional<SimpleClientDetails> findByClientId(@Param("clientId") String clientId);

    @Select("select count(1) from oauth_client_details d where d.client_id = #{clientId}")
    boolean existsByClientId(@Param("clientId") String clientId);
}
