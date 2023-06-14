package cc.powind.security.core.properties;

public class PageProperties {

    /**
     * 登录页
     */
    private LoginPage loginPage = new LoginPage();

    public LoginPage getLoginPage() {
        return loginPage;
    }

    public void setLoginPage(LoginPage loginPage) {
        this.loginPage = loginPage;
    }

    public static class LoginPage {

        /**
         * 默认的登录方式
         * account: 账户密码方式 verifyCode: 验证码方式 mobile: 手机号方式 email 密码方式
         */
        private String loginWay = "account";

        /**
         * 首页顶部的标题
         */
        private String title = "安全认证中心";

        /**
         * logo的标题
         */
        private String logoTitle = "安全认证管理系统";

        /**
         * logo图片的地址
         */
        private String logoImagePath = "/images/logo.png";

        /**
         * 背景图片
         */
        private String backendImagePath = "/images/bj001.jpg";

        /**
         * 版权说明
         */
        private String copyright = "Copyright © powind All Rights Reversed";

        /**
         * 版本号
         */
        private String version = "No. TC-01-23";

        /**
         * 辅助的样式文件
         */
        private String cssFragment = "";

        /**
         * 辅助的脚本文件
         */
        private String jsFragment = "";

        /**
         * 页面左侧的html片段
         */
        private String leftHtmlFragment = "";

        /**
         * 页面底部渲染的html片段
         */
        private String footerHtmlFragment = "";

        public String getLoginWay() {
            return loginWay;
        }

        public void setLoginWay(String loginWay) {
            this.loginWay = loginWay;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLogoTitle() {
            return logoTitle;
        }

        public void setLogoTitle(String logoTitle) {
            this.logoTitle = logoTitle;
        }

        public String getLogoImagePath() {
            return logoImagePath;
        }

        public void setLogoImagePath(String logoImagePath) {
            this.logoImagePath = logoImagePath;
        }

        public String getBackendImagePath() {
            return backendImagePath;
        }

        public void setBackendImagePath(String backendImagePath) {
            this.backendImagePath = backendImagePath;
        }

        public String getCopyright() {
            return copyright;
        }

        public void setCopyright(String copyright) {
            this.copyright = copyright;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getCssFragment() {
            return cssFragment;
        }

        public void setCssFragment(String cssFragment) {
            this.cssFragment = cssFragment;
        }

        public String getJsFragment() {
            return jsFragment;
        }

        public void setJsFragment(String jsFragment) {
            this.jsFragment = jsFragment;
        }

        public String getLeftHtmlFragment() {
            return leftHtmlFragment;
        }

        public void setLeftHtmlFragment(String leftHtmlFragment) {
            this.leftHtmlFragment = leftHtmlFragment;
        }

        public String getFooterHtmlFragment() {
            return footerHtmlFragment;
        }

        public void setFooterHtmlFragment(String footerHtmlFragment) {
            this.footerHtmlFragment = footerHtmlFragment;
        }
    }
}
