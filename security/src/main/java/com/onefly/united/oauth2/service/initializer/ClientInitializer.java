package com.onefly.united.oauth2.service.initializer;

import com.onefly.united.oauth2.dao.SimpleClientDetailDao;
import com.onefly.united.oauth2.domain.SimpleClientDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.IdGenerator;
import org.springframework.util.JdkIdGenerator;

import javax.annotation.PostConstruct;

@Component
@DependsOn("flywayInitializer")
public class ClientInitializer {

    private static final String OPENID_CLIENT = "openid";
    private static final String TRUSTED_CLIENT = "trusted";
    private static final String ANDROID = "android";
    private static final String IOS = "ios";

    private final SimpleClientDetailDao clientDetailRepository;

    private final PasswordEncoder passwordEncoder;
    private static final IdGenerator idGenerator = new JdkIdGenerator();


    @Autowired
    public ClientInitializer(SimpleClientDetailDao clientDetailRepository, PasswordEncoder passwordEncoder) {
        this.clientDetailRepository = clientDetailRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {

        createIfNotExists(OPENID_CLIENT, "693bb00a19134e3c9fc990bc9742f614", null,
                "authorization_code,password,refresh_token", "openid",
                "http://onefly.com,http://localhost:8080,http://172.16.2.28:8080,http://172.16.2.159:8080");

        createIfNotExists(TRUSTED_CLIENT, idGenerator.generateId().toString(), "ROLE_TRUSTED_CLIENT",
                "authorization_code,refresh_token", "openid",
                "http://onefly.com,http://hubi.com,http://cgod.me");
        createIfNotExists(ANDROID, idGenerator.generateId().toString(), "ANDROID",
                "client_credentials,password", "app", "");
        createIfNotExists(IOS, idGenerator.generateId().toString(), "IOS",
                "client_credentials,password", "app", "");
    }

    private void createIfNotExists(String clientId, String secret, String authorities, String authorizedGrantTypes, String scopes, String uri) {
        boolean exists = clientDetailRepository.existsByClientId(clientId);
        if (!exists) {
            SimpleClientDetails client = new SimpleClientDetails();
            client.setClientId(clientId);
            client.setClientSecretOriginal(secret);
            client.setClientSecret(passwordEncoder.encode(secret));
            client.setAuthorities(authorities);
            client.setAuthorizedGrantTypes(authorizedGrantTypes);
            client.setScopes(scopes);
            client.setAutoApprove(true);
            client.setWebServerRedirectUri(uri);
            clientDetailRepository.insert(client);
        }
    }
}
