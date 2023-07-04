package cc.powind.security.core.login;

import cc.powind.security.core.transfer.ReturnTypeEnum;
import org.springframework.http.HttpStatus;
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

    private String returnTypeParameterName = "returnType";

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getReturnTypeParameterName() {
        return returnTypeParameterName;
    }

    public void setReturnTypeParameterName(String returnTypeParameterName) {
        this.returnTypeParameterName = returnTypeParameterName;
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

        String returnType = request.getParameter(returnTypeParameterName);
        if (ReturnTypeEnum.JSON_TYPE.getValue().equalsIgnoreCase(returnType)) {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpStatus.OK.value());
            response.getWriter().write("{\"redirectUrl\": \"" + redirectUrl + "\"}");
        } else {
            redirectStrategy.sendRedirect(request, response, redirectUrl);
        }
    }
}
