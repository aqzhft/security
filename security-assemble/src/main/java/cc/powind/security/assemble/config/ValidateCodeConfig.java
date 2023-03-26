package cc.powind.security.assemble.config;

import cc.powind.security.assemble.properties.SecurityProperties;
import cc.powind.security.token.InMemoryTokenRepository;
import cc.powind.security.token.TokenEntryPoint;
import cc.powind.security.token.TokenIntercept;
import cc.powind.security.token.model.*;
import cc.powind.security.token.service.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.List;

public class ValidateCodeConfig {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private SecurityProperties properties;

    @Bean
    public TokenEntryPoint tokenEntryPoint() {
        return new TokenEntryPoint();
    }

    @Bean(initMethod = "init")
    public TokenService<ImageToken> imageCodeService() {
        ImageTokenService imageCodeService = new ImageTokenService();
        imageCodeService.setTokenRepository(new InMemoryTokenRepository());
        imageCodeService.setInterceptUrls(properties.getValidator().getImage().getInterceptUrls());
        imageCodeService.getInterceptUrls().add(properties.getPath().getFormLoginUrl() + ":post");
        return imageCodeService;
    }

    @Bean(initMethod = "init")
    public TokenService<SmsToken> smsCodeService() {
        SmsTokenService smsCodeService = new SmsTokenService();
        smsCodeService.setTokenRepository(new InMemoryTokenRepository());
        smsCodeService.setInterceptUrls(properties.getValidator().getSms().getInterceptUrls());
        smsCodeService.getInterceptUrls().add(properties.getPath().getMobileLoginUrl() + ":post");
        return smsCodeService;
    }

    @Bean(initMethod = "init")
    public TokenService<FormToken> formCodeService() {
        FormTokenService formCodeService = new FormTokenService();
        formCodeService.setTokenRepository(new InMemoryTokenRepository());
        formCodeService.setInterceptUrls(properties.getValidator().getForm().getInterceptUrls());
        return formCodeService;
    }

    @Bean(initMethod = "init")
    public TokenService<VerifyToken> verifyCodeService() {
        VerifyTokenService verifyCodeService = new VerifyTokenService();
        verifyCodeService.setTokenRepository(new InMemoryTokenRepository());
        verifyCodeService.setInterceptUrls(properties.getValidator().getVerify().getInterceptUrls());
        verifyCodeService.getInterceptUrls().add(properties.getPath().getVerifyLoginUrl() + ":post");
        return verifyCodeService;
    }

    @Bean
    public TokenIntercept tokenIntercept(List<TokenService<? extends Token>> tokenServices) {
        return new TokenIntercept(tokenServices);
    }
}
