package cc.powind.security.assemble.config;

import cc.powind.security.assemble.properties.SecurityProperties;
import cc.powind.security.core.login.DefaultAuthenticationFailureHandler;
import cc.powind.security.core.login.DefaultAuthenticationSuccessHandler;
import cc.powind.security.core.login.LoginInfoService;
import cc.powind.security.core.login.sms.SmsCodeAuthenticationConfig;
import cc.powind.security.core.login.verify.VerifyCodeAuthenticationConfig;
import cc.powind.security.core.login.wxwork.WxworkAuthenticationConfig;
import cc.powind.security.core.login.wxwork.WxworkAuthenticationFilter;
import cc.powind.security.core.login.wxwork.WxworkAuthenticationProvider;
import cc.powind.security.core.login.wxwork.WxworkOAuth2RedirectFilter;
import cc.powind.security.core.logout.DefaultLogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.client.RestTemplate;

public class LoginConfig {

    @Autowired
    private SecurityProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        DefaultAuthenticationSuccessHandler handler = new DefaultAuthenticationSuccessHandler();
        handler.setHomePage(properties.getPath().getHomePage());
        return handler;
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new DefaultAuthenticationFailureHandler();
    }

    @Bean
    public AuthenticationSuccessHandler oauth2SuccessHandler() {
        DefaultAuthenticationSuccessHandler handler = new DefaultAuthenticationSuccessHandler();
        handler.setHomePage(properties.getPath().getHomePage());
        handler.setIgnorePaths(new String[]{"/oauth2/authorize"});
        return handler;
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        DefaultLogoutSuccessHandler handler = new DefaultLogoutSuccessHandler();
        handler.setLoginPage(properties.getPath().getBasePath() + properties.getPath().getLoginPage());
        return handler;
    }

    @Bean
    public SmsCodeAuthenticationConfig smsCodeAuthenticationConfig (
            LoginInfoService loginInfoService
    ) {
        SmsCodeAuthenticationConfig config = new SmsCodeAuthenticationConfig();
        config.setAuthenticationSuccessHandler(authenticationSuccessHandler());
        config.setAuthenticationFailureHandler(authenticationFailureHandler());
        config.setPatterns(new String[] {properties.getPath().getMobileLoginUrl(),  properties.getPath().getEmailLoginUrl()});
        config.setLoginInfoService(loginInfoService);
        return config;
    }

    @Bean
    public WxworkAuthenticationConfig wxworkAuthenticationConfig(UserDetailsService userDetailsService) {
        WxworkAuthenticationConfig config = new WxworkAuthenticationConfig();
        config.setWxworkAuthenticationFilter(wxworkAuthenticationFilter());
        config.setWxworkOAuth2RedirectFilter(wxworkOAuth2RedirectFilter());
        config.setAuthenticationSuccessHandler(authenticationSuccessHandler());
        config.setAuthenticationFailureHandler(authenticationFailureHandler());
        config.setWxworkAuthenticationProvider(wxworkAuthenticationProvider(userDetailsService));
        return config;
    }

    private WxworkAuthenticationFilter wxworkAuthenticationFilter() {
        return new WxworkAuthenticationFilter();
    }

    private WxworkAuthenticationProvider wxworkAuthenticationProvider(UserDetailsService userDetailsService) {
        WxworkAuthenticationProvider provider = new WxworkAuthenticationProvider();
        provider.setRestOperations(new RestTemplate());
        provider.setUserDetailsService(userDetailsService);
        provider.setCorpId(properties.getWxwork().getCorpId());
        provider.setCorpSecret(properties.getWxwork().getCorpSecret());
        provider.setTokenUri(properties.getWxwork().getTokenUri());
        provider.setUserInfoUri(properties.getWxwork().getUserInfoUri());
        return provider;
    }

    private WxworkOAuth2RedirectFilter wxworkOAuth2RedirectFilter() {
        WxworkOAuth2RedirectFilter filter = new WxworkOAuth2RedirectFilter();
        filter.setCorpId(properties.getWxwork().getCorpId());
        filter.setAgentId(properties.getWxwork().getAgentId());
        filter.setRedirectUri(properties.getWxwork().getRedirectUri());
        filter.setAuthorizationUri(properties.getWxwork().getAuthorizationUri());
        filter.setAuthorizationQrcodeUri(properties.getWxwork().getAuthorizationQrcodeUri());
        return filter;
    }

    @Bean
    public VerifyCodeAuthenticationConfig verifyCodeAuthenticationConfig (
            LoginInfoService loginInfoService
    ) {
        VerifyCodeAuthenticationConfig config = new VerifyCodeAuthenticationConfig();
        config.setAuthenticationSuccessHandler(authenticationSuccessHandler());
        config.setAuthenticationFailureHandler(authenticationFailureHandler());
        config.setLoginInfoService(loginInfoService);
        config.setPattern(properties.getPath().getVerifyLoginUrl());
        return config;
    }
}
