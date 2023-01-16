package cc.powind.security.core.config;

import cc.powind.security.core.properties.SecurityProperties;
import cc.powind.security.core.validator.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

public class ValidateCodeConfig {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private SecurityProperties properties;

    @Bean
    public ValidateCodeEntryPoint validateCodeEntryPoint() {
        return new ValidateCodeEntryPoint();
    }

    @Bean
    @ConditionalOnMissingBean
    public ValidateCodeSender defaultValidateCodeSender() {
        return (validateCode, types, response) -> {
            log.info("default validate code sender, detail is " + validateCode);
        };
    }

    @Bean
    public ValidateCodeService<ImageCode> imageCodeService() {
        ImageCodeService imageCodeService = new ImageCodeService();
        imageCodeService.setValidateCodeRepository(new InMemoryValidateCodeRepository());
        imageCodeService.setInterceptUrls(properties.getValidator().getImage().getInterceptUrls());
        return imageCodeService;
    }

    @Bean
    public ValidateCodeService<SmsCode> smsCodeService(ValidateCodeSender validateCodeSender) {
        SmsCodeService smsCodeService = new SmsCodeService();
        smsCodeService.setValidateCodeRepository(new InMemoryValidateCodeRepository());
        smsCodeService.setInterceptUrls(properties.getValidator().getSms().getInterceptUrls());
        smsCodeService.setValidateCodeSender(validateCodeSender);
        return smsCodeService;
    }

    @Bean
    public ValidateCodeService<FormCode> formCodeService() {
        FormCodeService formCodeService = new FormCodeService();
        formCodeService.setValidateCodeRepository(new InMemoryValidateCodeRepository());
        formCodeService.setInterceptUrls(properties.getValidator().getForm().getInterceptUrls());
        return formCodeService;
    }

    @Bean
    public ValidateCodeService<VerifyCode> verifyCodeService(ValidateCodeSender validateCodeSender) {
        VerifyCodeService verifyCodeService = new VerifyCodeService();
        verifyCodeService.setValidateCodeRepository(new InMemoryValidateCodeRepository());
        verifyCodeService.setInterceptUrls(properties.getValidator().getVerify().getInterceptUrls());
        verifyCodeService.setValidateCodeSender(validateCodeSender);
        return verifyCodeService;
    }
}
