package cc.powind.security.core.properties;

public class PathProperties {

    /**
     * basePath
     */
    private String basePath = "";

    /**
     * 主页
     */
    private String homePage = "/home";

    /**
     * 登录页
     */
    private String loginPage = "/login";

    /**
     * 表单登录提交地址
     */
    private String formLoginUrl = "/login/form";

    /**
     * 手机号登录提交地址
     */
    private String mobileLoginUrl = "/login/mobile";

    /**
     * 验证码登录提交地址
     */
    private String verifyLoginUrl = "/login/verify";

    /**
     * 登出地址
     */
    private String logoutUrl = "/logout";

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getLoginPage() {
        return loginPage;
    }

    public void setLoginPage(String loginPage) {
        this.loginPage = loginPage;
    }

    public String getFormLoginUrl() {
        return formLoginUrl;
    }

    public void setFormLoginUrl(String formLoginUrl) {
        this.formLoginUrl = formLoginUrl;
    }

    public String getMobileLoginUrl() {
        return mobileLoginUrl;
    }

    public void setMobileLoginUrl(String mobileLoginUrl) {
        this.mobileLoginUrl = mobileLoginUrl;
    }

    public String getVerifyLoginUrl() {
        return verifyLoginUrl;
    }

    public void setVerifyLoginUrl(String verifyLoginUrl) {
        this.verifyLoginUrl = verifyLoginUrl;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }
}
