package cc.powind.security.core.config;

import cc.powind.security.core.login.DefaultAuthenticationFailureHandler;
import cc.powind.security.core.login.DefaultAuthenticationSuccessHandler;
import cc.powind.security.core.login.sms.SmsCodeAuthenticationConfig;
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
        handler.setMainPage("/main-page");
        return handler;
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new DefaultAuthenticationFailureHandler();
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new DefaultLogoutSuccessHandler();
    }

    @Bean
    public SmsCodeAuthenticationConfig smsCodeAuthenticationConfig (
            UserDetailsService userDetailsService,
            AuthenticationSuccessHandler authenticationSuccessHandler,
            AuthenticationFailureHandler authenticationFailureHandler
    ) {
        SmsCodeAuthenticationConfig config = new SmsCodeAuthenticationConfig();
        config.setUserDetailsService(userDetailsService);
        config.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        config.setAuthenticationFailureHandler(authenticationFailureHandler);
        return config;
    }

    @Bean
    public WxworkAuthenticationConfig wxworkAuthenticationConfig() {
        WxworkAuthenticationConfig wxworkAuthenticationConfig = new WxworkAuthenticationConfig();
        wxworkAuthenticationConfig.setProperties(properties.getWxwork());
        return wxworkAuthenticationConfig;
    }
}
