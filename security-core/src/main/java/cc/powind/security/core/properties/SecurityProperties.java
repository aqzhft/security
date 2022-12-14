package cc.powind.security.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.UUID;

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

    /**
     * 登出地址
     */
    private String logoutUrl = "/logout";

    /**
     * 校验码
     */
    private Validator validator = new Validator();

    /**
     * 记住密码
     */
    private RememberMe rememberMe = new RememberMe();

    /**
     * 会话
     */
    private Session session = new Session();

    /**
     * 企业微信
     */
    private WxworkProperties wxwork = new WxworkProperties();

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

    public RememberMe getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(RememberMe rememberMe) {
        this.rememberMe = rememberMe;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public WxworkProperties getWxwork() {
        return wxwork;
    }

    public void setWxwork(WxworkProperties wxwork) {
        this.wxwork = wxwork;
    }

    public static class Validator {

        private ImageValidator image = new ImageValidator();

        private SmsValidator sms = new SmsValidator();

        private FormValidator form = new FormValidator();

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

        public FormValidator getForm() {
            return form;
        }

        public void setForm(FormValidator form) {
            this.form = form;
        }
    }

    public static class RememberMe {

        /**
         * 记住密码的加密key
         * 分布式情况下不能使用随机数，必须要指定
         */
        private String key = getUUID();

        /**
         * token的过期时间 默认一周
         */
        private int tokenValiditySeconds = 7 * 24 * 3600;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public int getTokenValiditySeconds() {
            return tokenValiditySeconds;
        }

        public void setTokenValiditySeconds(int tokenValiditySeconds) {
            this.tokenValiditySeconds = tokenValiditySeconds;
        }
    }

    public static class Session {

        /**
         * 单个账户同时在线的限制数量
         * 一般情况下移动端和web端可以同时登录，但是h5页面的访问和web端无异并不一定需要通过oAuth2协议
         * 所以针对此种情况需要手工区分是移动端还是web端
         * 基于oAuth2协议并不受此影响
         */
        private int maximumSessions = -1;

        public int getMaximumSessions() {
            return maximumSessions;
        }

        public void setMaximumSessions(int maximumSessions) {
            this.maximumSessions = maximumSessions;
        }
    }

    private static String getUUID () {
        return UUID.randomUUID().toString().toLowerCase().replaceAll("-", "");
    }
}
