package cc.powind.security.core.login.wxwork;

import org.apache.commons.lang3.StringUtils;

public class AgentInfo {

    private String id;

    private String secret;

    private String authorizationUri;

    private String redirectUri;

    public AgentInfo() {}

    public AgentInfo(String id, String secret, String authorizationUri, String redirectUri) {
        this.id = id;
        this.secret = secret;
        this.authorizationUri = authorizationUri;
        this.redirectUri = redirectUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getAuthorizationUri() {
        return authorizationUri;
    }

    public void setAuthorizationUri(String authorizationUri) {
        this.authorizationUri = authorizationUri;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String generateURI(String corpId) {

        // There is a serious problem here, where the parameters for link redirection and scanning are inconsistent,
        // which can only be determined through such a simple method
        if (StringUtils.contains(authorizationUri, "qrConnect")) {
            return authorizationUri + "?appid=" + corpId + "&agentid=" + id + "&redirect_uri=" + redirectUri + "&state=STATE";
        }

        return authorizationUri + "?appid=" + corpId + "&redirect_uri=" + redirectUri + "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
    }
}
