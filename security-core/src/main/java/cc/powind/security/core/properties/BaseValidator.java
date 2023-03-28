package cc.powind.security.core.properties;

import java.util.ArrayList;
import java.util.List;

public class BaseValidator {

    private int len = 4;

    private int timeout = 300;

    private List<String> interceptUrls = new ArrayList<>();

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public List<String> getInterceptUrls() {
        return interceptUrls;
    }

    public void setInterceptUrls(List<String> interceptUrls) {
        this.interceptUrls = interceptUrls;
    }
}
