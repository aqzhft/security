package cc.powind.security.core.authorize;

import org.springframework.security.core.GrantedAuthority;

public class RestGrantedAuthority implements GrantedAuthority {

    private static final String SPLIT_CHAR = ":";

    private String path;

    private String method;

    public RestGrantedAuthority() {
    }

    public RestGrantedAuthority(String path, String method) {
        this.path = path;
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String getAuthority() {
        return method + SPLIT_CHAR + path;
    }
}
