package cc.powind.security.core.config;

import cc.powind.security.core.properties.SecurityProperties;
import cc.powind.security.core.validator.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

public class ValidateCodeConfig {

    @Autowired
    private SecurityProperties properties;

    @Bean
    public ValidateCodeEntryPoint validateCodeEntryPoint() {
        return new ValidateCodeEntryPoint();
    }

    @Bean
    public ValidateCodeService<ImageCode> imageCodeService() {
        ImageCodeService imageCodeService = new ImageCodeService();
        imageCodeService.setValidateCodeRepository(new InMemoryValidateCodeRepository());
        imageCodeService.setInterceptUrls(properties.getValidator().getImage().getInterceptUrls());
        return imageCodeService;
    }

    @Bean
    public ValidateCodeService<SmsCode> smsCodeService() {
        SmsCodeService smsCodeService = new SmsCodeService();
        smsCodeService.setValidateCodeRepository(new InMemoryValidateCodeRepository());
        smsCodeService.setInterceptUrls(properties.getValidator().getSms().getInterceptUrls());
        return smsCodeService;
    }

    @Bean
    public ValidateCodeService<FormCode> formCodeService() {
        FormCodeService formCodeService = new FormCodeService();
        formCodeService.setValidateCodeRepository(new InMemoryValidateCodeRepository());
        formCodeService.setInterceptUrls(properties.getValidator().getForm().getInterceptUrls());
        return formCodeService;
    }
}
