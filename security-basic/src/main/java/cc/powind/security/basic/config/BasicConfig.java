package cc.powind.security.basic.config;

import cc.powind.security.assemble.config.AssembleConfigureAdapter;
import cc.powind.security.assemble.config.SecurityConfig;
import cc.powind.security.core.login.LoginInfo;
import cc.powind.security.core.login.LoginInfoImpl;
import cc.powind.security.core.login.LoginInfoService;
import cc.powind.security.core.login.SecurityUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration
@Import(SecurityConfig.class)
public class BasicConfig extends AssembleConfigureAdapter {

    /**
     * loginId作为系统内访问的唯一标识
     */
    public static final List<SecurityUserInfo> userCache = new ArrayList<>();

    @Autowired(required = false) PasswordEncoder passwordEncoder;

    /**
     * 三种登录方式： username、mobile、email
     *
     */
    private static final List<LoginInfo> loginInfoSet = new ArrayList<>();

    @PostConstruct
    public void init() {

        userCache.add(createSecurityUserInfo("1", "admin", passwordEncoder.encode("admin"), "15212345678", "admin@admin.com"));
        userCache.add(createSecurityUserInfo("2", "lisa", passwordEncoder.encode("lisa"), "15212341234", "lisa@lisa.com"));
        userCache.add(createSecurityUserInfo("3", "tom", passwordEncoder.encode("tom"), "15211112222", "tom@tom.com"));

        for (SecurityUserInfo info : userCache) {

            loginInfoSet.add(new LoginInfoImpl(info, info.getName(), LoginInfoService.Type.PASSWORD));

            loginInfoSet.add(new LoginInfoImpl(info, info.getMobile(), LoginInfoService.Type.MOBILE));

            loginInfoSet.add(new LoginInfoImpl(info, info.getEmail(), LoginInfoService.Type.EMAIL));
        }

        loginInfoSet.add(new LoginInfoImpl(userCache.get(1), "167", LoginInfoService.Type.GITLAB));
    }

    private SecurityUserInfo createSecurityUserInfo(String loginId, String name, String password, String mobile, String email) {
        SecurityUserInfo userInfo = new SecurityUserInfo();
        userInfo.setLoginId(loginId);
        userInfo.setUsername(name);
        userInfo.setPassword(password);
        userInfo.setMobile(mobile);
        userInfo.setEmail(email);
        // userInfo.getAuthorities().add(new SimpleGrantedAuthority("ADMIN"));
        return userInfo;
    }

    @Override
    protected Map<String, LoginInfo> loginInfoMappings() {
        return loginInfoSet.stream().collect(Collectors.toMap(LoginInfo::getIdentifyId, bean -> bean, (a, b) -> a));
    }

    @Override
    public RegisteredClientRepository authorizationClientRepository() {
        RegisteredClient oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("oidc-client")
                .clientSecret(passwordEncoder.encode("secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri("http://localhost:8080/login/oauth2/code/custom")
                // .postLogoutRedirectUri("http://127.0.0.1:8080/")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope(OidcScopes.EMAIL)
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();

        return new InMemoryRegisteredClientRepository(oidcClient);
    }
}
