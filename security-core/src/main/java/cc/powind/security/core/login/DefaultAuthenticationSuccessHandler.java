package cc.powind.security.core.login;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DefaultAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final RequestCache requestCache = new HttpSessionRequestCache();

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private String homePage;

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        SavedRequest savedRequest = requestCache.getRequest(request, response);
        String redirectUrl = savedRequest == null ? null : savedRequest.getRedirectUrl();

        // 如果跳转地址是空 则跳转到主页
        redirectUrl = (redirectUrl == null || "".equals(redirectUrl)) ? homePage : redirectUrl;

        // 如果主页也没设置 则跳转到根路径
        if (redirectUrl == null || "".equals(redirectUrl)) {
            redirectUrl = "";
        }

        redirectStrategy.sendRedirect(request, response, redirectUrl);
    }
}
