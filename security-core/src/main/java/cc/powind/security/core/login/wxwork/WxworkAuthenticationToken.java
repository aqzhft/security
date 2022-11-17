package cc.powind.security.core.login.wxwork;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;

public class WxworkAuthenticationToken extends AbstractAuthenticationToken {

    private String userId;

    private String code;

    public WxworkAuthenticationToken(String code) {
        super(Collections.emptyList());
        this.code = code;
        this.setAuthenticated(false);
    }

    @Override
    public String getPrincipal() {
        return this.userId;
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
