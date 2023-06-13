package cc.powind.security.assembly.config;

import cc.powind.security.assembly.properties.SecurityProperties;
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

    @Autowired
    private TokenNotifier tokenNotifier;

    @Bean
    public TokenEntryPoint tokenEntryPoint() {
        return new TokenEntryPoint();
    }

    @Bean(initMethod = "init")
    public TokenService<ImageToken> imageTokenService() {
        ImageTokenService imageTokenService = new ImageTokenService();
        imageTokenService.setTokenRepository(new InMemoryTokenRepository());
        imageTokenService.setInterceptUrls(properties.getValidator().getImage().getInterceptUrls());
        imageTokenService.getInterceptUrls().add(properties.getPath().getFormLoginUrl() + ":post");
        imageTokenService.setLen(properties.getValidator().getImage().getLen());
        imageTokenService.setTimeout(properties.getValidator().getImage().getTimeout());
        imageTokenService.setTokenNotifier(tokenNotifier);
        return imageTokenService;
    }

    @Bean(initMethod = "init")
    public TokenService<SmsToken> smsTokenService() {
        SmsTokenService smsTokenService = new SmsTokenService();
        smsTokenService.setTokenRepository(new InMemoryTokenRepository());
        smsTokenService.setInterceptUrls(properties.getValidator().getSms().getInterceptUrls());
        smsTokenService.getInterceptUrls().add(properties.getPath().getMobileLoginUrl() + ":post");
        smsTokenService.setLen(properties.getValidator().getSms().getLen());
        smsTokenService.setTimeout(properties.getValidator().getSms().getTimeout());
        smsTokenService.setTokenNotifier(tokenNotifier);
        return smsTokenService;
    }

    @Bean(initMethod = "init")
    public TokenService<FormToken> formTokenService() {
        FormTokenService formTokenService = new FormTokenService();
        formTokenService.setTokenRepository(new InMemoryTokenRepository());
        formTokenService.setInterceptUrls(properties.getValidator().getForm().getInterceptUrls());
        formTokenService.setLen(properties.getValidator().getForm().getLen());
        formTokenService.setTimeout(properties.getValidator().getForm().getTimeout());
        formTokenService.setTokenNotifier(tokenNotifier);
        return formTokenService;
    }

    @Bean(initMethod = "init")
    public TokenService<VerifyToken> verifyTokenService() {
        VerifyTokenService verifyTokenService = new VerifyTokenService();
        verifyTokenService.setTokenRepository(new InMemoryTokenRepository());
        verifyTokenService.setInterceptUrls(properties.getValidator().getVerify().getInterceptUrls());
        verifyTokenService.getInterceptUrls().add(properties.getPath().getVerifyLoginUrl() + ":post");
        verifyTokenService.setLen(properties.getValidator().getVerify().getLen());
        verifyTokenService.setTimeout(properties.getValidator().getVerify().getTimeout());
        verifyTokenService.setTokenNotifier(tokenNotifier);
        return verifyTokenService;
    }

    @Bean(initMethod = "init")
    public TokenService<EmailToken> emailTokenService() {
        EmailTokenService emailTokenService = new EmailTokenService();
        emailTokenService.setTokenRepository(new InMemoryTokenRepository());
        emailTokenService.setInterceptUrls(properties.getValidator().getEmail().getInterceptUrls());
        emailTokenService.getInterceptUrls().add(properties.getPath().getEmailLoginUrl() + ":post");
        emailTokenService.setLen(properties.getValidator().getEmail().getLen());
        emailTokenService.setTimeout(properties.getValidator().getEmail().getTimeout());
        emailTokenService.setTokenNotifier(tokenNotifier);
        return emailTokenService;
    }

    @Bean
    public TokenIntercept tokenIntercept(List<TokenService<? extends Token>> tokenServices) {
        return new TokenIntercept(tokenServices);
    }
}
