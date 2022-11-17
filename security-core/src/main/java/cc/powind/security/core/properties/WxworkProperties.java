package cc.powind.security.core.properties;

public class WxworkProperties {

    /**
     * 企业ID
     */
    private String corpId;

    /**
     * 密钥
     */
    private String corpSecret;

    /**
     * 应用ID
     */
    private String agentId;

    /**
     * 获取accessToken的地址
     */
    private String tokenUri;

    /**
     * 获取用户信息的地址
     */
    private String userInfoUri;

    /**
     * 页面点击跳转登录地址
     */
    private String authorizationUri;

    /**
     * 扫码登录地址
     */
    private String authorizationQrcodeUri;

    /**
     * 授权回调地址
     */
    private String redirectUri;

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getCorpSecret() {
        return corpSecret;
    }

    public void setCorpSecret(String corpSecret) {
        this.corpSecret = corpSecret;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getTokenUri() {
        return tokenUri;
    }

    public void setTokenUri(String tokenUri) {
        this.tokenUri = tokenUri;
    }

    public String getUserInfoUri() {
        return userInfoUri;
    }

    public void setUserInfoUri(String userInfoUri) {
        this.userInfoUri = userInfoUri;
    }

    public String getAuthorizationUri() {
        return authorizationUri;
    }

    public void setAuthorizationUri(String authorizationUri) {
        this.authorizationUri = authorizationUri;
    }

    public String getAuthorizationQrcodeUri() {
        return authorizationQrcodeUri;
    }

    public void setAuthorizationQrcodeUri(String authorizationQrcodeUri) {
        this.authorizationQrcodeUri = authorizationQrcodeUri;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
}
