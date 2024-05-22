package cc.powind.security.core.login.wxwork;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;

public class WxworkAuthenticationToken extends AbstractAuthenticationToken {

    private Object principal;

    private String code;

    private String agentId;

    public WxworkAuthenticationToken(String code, String agentId) {
        super(Collections.emptyList());
        this.code = code;
        this.agentId = agentId;
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

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }
}
