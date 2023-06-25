package cc.powind.security.assembly.properties;

import cc.powind.security.core.properties.BaseValidator;
import cc.powind.security.core.properties.PageProperties;
import cc.powind.security.core.properties.PathProperties;
import cc.powind.security.core.properties.WxworkProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.UUID;

@ConfigurationProperties("security")
public class SecurityProperties {

    private String[] loginWays;

    /**
     * 路径配置
     */
    private PathProperties path = new PathProperties();

    /**
     * 页面显示信息配置
     */
    private PageProperties page = new PageProperties();

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

    /**
     * 不需要认证授权的路径
     */
    private String[] noAuthPath = new String[] {};

    /**
     * 不需要检查权限的路径 path:method
     */
    private String[] noCheckPath = new String[] {};

    public String[] getLoginWays() {
        return loginWays;
    }

    public void setLoginWays(String[] loginWays) {
        this.loginWays = loginWays;
    }

    public PathProperties getPath() {
        return path;
    }

    public void setPath(PathProperties path) {
        this.path = path;
    }

    public PageProperties getPage() {
        return page;
    }

    public void setPage(PageProperties page) {
        this.page = page;
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

    public String[] getNoAuthPath() {
        return noAuthPath;
    }

    public void setNoAuthPath(String[] noAuthPath) {
        this.noAuthPath = noAuthPath;
    }

    public String[] getNoCheckPath() {
        return noCheckPath;
    }

    public void setNoCheckPath(String[] noCheckPath) {
        this.noCheckPath = noCheckPath;
    }

    public static class Validator {

        private BaseValidator image = new BaseValidator();

        private BaseValidator sms = new BaseValidator();

        private BaseValidator form = new BaseValidator();

        private BaseValidator verify = new BaseValidator();

        private BaseValidator email = new BaseValidator();

        public BaseValidator getImage() {
            return image;
        }

        public void setImage(BaseValidator image) {
            this.image = image;
        }

        public BaseValidator getSms() {
            return sms;
        }

        public void setSms(BaseValidator sms) {
            this.sms = sms;
        }

        public BaseValidator getForm() {
            return form;
        }

        public void setForm(BaseValidator form) {
            this.form = form;
        }

        public BaseValidator getVerify() {
            return verify;
        }

        public void setVerify(BaseValidator verify) {
            this.verify = verify;
        }

        public BaseValidator getEmail() {
            return email;
        }

        public void setEmail(BaseValidator email) {
            this.email = email;
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
