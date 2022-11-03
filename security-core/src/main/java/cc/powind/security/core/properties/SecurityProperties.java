package cc.powind.security.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("security")
public class SecurityProperties {

    /**
     * 登录页
     */
    private String loginPage = "/authentication/require";

    /**
     * 登录请求地址
     */
    private String loginProcessUrl = "/authentication/form";

    private String logoutUrl = "/logout";

    private Validator validator = new Validator();

    public String getLoginPage() {
        return loginPage;
    }

    public void setLoginPage(String loginPage) {
        this.loginPage = loginPage;
    }

    public String getLoginProcessUrl() {
        return loginProcessUrl;
    }

    public void setLoginProcessUrl(String loginProcessUrl) {
        this.loginProcessUrl = loginProcessUrl;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public static class Validator {

        private ImageValidator image = new ImageValidator();

        private SmsValidator sms = new SmsValidator();

        public ImageValidator getImage() {
            return image;
        }

        public void setImage(ImageValidator image) {
            this.image = image;
        }

        public SmsValidator getSms() {
            return sms;
        }

        public void setSms(SmsValidator sms) {
            this.sms = sms;
        }
    }
}
