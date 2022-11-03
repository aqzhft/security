package cc.powind.security.core.properties;

import java.util.ArrayList;
import java.util.List;

public class ImageValidator {

    private List<String> interceptUrls = new ArrayList<>();

    public List<String> getInterceptUrls() {
        return interceptUrls;
    }

    public void setInterceptUrls(List<String> interceptUrls) {
        this.interceptUrls = interceptUrls;
    }
}
