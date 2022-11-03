package cc.powind.security.core.config;

import cc.powind.security.core.authentication.SmsCodeAuthenticationConfig;
import cc.powind.security.core.handler.DefaultAuthenticationFailureHandler;
import cc.powind.security.core.handler.DefaultAuthenticationSuccessHandler;
import cc.powind.security.core.handler.DefaultLogoutSuccessHandler;
import cc.powind.security.core.properties.SecurityProperties;
import cc.powind.security.core.validator.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
public class BasicConfig {

    @Autowired
    private SecurityProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = "smsCodeRepository")
    public ValidateCodeRepository smsCodeRepository() {
        return new InMemoryValidateCodeRepository();
    }

    @Bean(name = "imageCodeRepository")
    public ValidateCodeRepository imageCodeRepository() {
        return new InMemoryValidateCodeRepository();
    }

    @Bean
    public ValidateCodeService<ImageCode> imageCodeService(@Autowired @Qualifier("imageCodeRepository") ValidateCodeRepository repository) {
        ImageCodeService imageCodeService = new ImageCodeService();
        imageCodeService.setValidateCodeRepository(repository);
        imageCodeService.setInterceptUrls(properties.getValidator().getImage().getInterceptUrls());
        return imageCodeService;
    }

    @Bean
    public ValidateCodeService<SmsCode> smsCodeService(@Autowired @Qualifier("smsCodeRepository") ValidateCodeRepository repository) {
        SmsCodeService smsCodeService = new SmsCodeService();
        smsCodeService.setValidateCodeRepository(repository);
        smsCodeService.setInterceptUrls(properties.getValidator().getSms().getInterceptUrls());
        return smsCodeService;
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
}
