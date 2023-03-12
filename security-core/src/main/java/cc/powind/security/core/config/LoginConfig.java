package cc.powind.security.core.config;

import cc.powind.security.core.login.DefaultAuthenticationFailureHandler;
import cc.powind.security.core.login.DefaultAuthenticationSuccessHandler;
import cc.powind.security.core.login.sms.SmsCodeAuthenticationConfig;
import cc.powind.security.core.login.verify.VerifyCodeAuthenticationConfig;
import cc.powind.security.core.login.wxwork.WxworkAuthenticationConfig;
import cc.powind.security.core.logout.DefaultLogoutSuccessHandler;
import cc.powind.security.core.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

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
        handler.setHomePage(properties.getPath().getBasePath() + properties.getPath().getHomePage());
        return handler;
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new DefaultAuthenticationFailureHandler();
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        DefaultLogoutSuccessHandler handler = new DefaultLogoutSuccessHandler();
        handler.setLoginPage(properties.getPath().getBasePath() + properties.getPath().getLoginPage());
        return handler;
    }

    @Bean
    public SmsCodeAuthenticationConfig smsCodeAuthenticationConfig (
            AuthenticationSuccessHandler authenticationSuccessHandler,
            AuthenticationFailureHandler authenticationFailureHandler
    ) {
        SmsCodeAuthenticationConfig config = new SmsCodeAuthenticationConfig();
        config.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        config.setAuthenticationFailureHandler(authenticationFailureHandler);
        config.setPattern(properties.getPath().getMobileLoginUrl());
        return config;
    }

    @Bean
    public WxworkAuthenticationConfig wxworkAuthenticationConfig(UserDetailsService userDetailsService) {
        WxworkAuthenticationConfig wxworkAuthenticationConfig = new WxworkAuthenticationConfig();
        wxworkAuthenticationConfig.setProperties(properties.getWxwork());
        wxworkAuthenticationConfig.setUserDetailsService(userDetailsService);
        return wxworkAuthenticationConfig;
    }

    @Bean
    public VerifyCodeAuthenticationConfig verifyCodeAuthenticationConfig (
            AuthenticationSuccessHandler authenticationSuccessHandler,
            AuthenticationFailureHandler authenticationFailureHandler,
            UserDetailsService userDetailsService
    ) {
        VerifyCodeAuthenticationConfig config = new VerifyCodeAuthenticationConfig();
        config.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        config.setAuthenticationFailureHandler(authenticationFailureHandler);
        config.setUserDetailsService(userDetailsService);
        config.setPattern(properties.getPath().getVerifyLoginUrl());
        return config;
    }
}
