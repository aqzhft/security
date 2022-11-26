package cc.powind.security.core.login.wxwork;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;

public class WxworkAuthenticationToken extends AbstractAuthenticationToken {

    private Object principal;

    private String code;

    public WxworkAuthenticationToken(String code) {
        super(Collections.emptyList());
        this.code = code;
        this.setAuthenticated(false);
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public void setPrincipal(Object principal) {
        this.principal = principal;
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
