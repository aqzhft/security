package cc.powind.security.core.login;

import cc.powind.security.core.transfer.ReturnTypeEnum;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class DefaultAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final RequestCache requestCache = new HttpSessionRequestCache();

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private final PathMatcher pathMatcher = new AntPathMatcher();

    private String homePage;

    // e.g. /oauth2/authorize
    private String[] ignorePaths;

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

    public String[] getIgnorePaths() {
        return ignorePaths;
    }

    public void setIgnorePaths(String[] ignorePaths) {
        this.ignorePaths = ignorePaths;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        SavedRequest savedRequest = requestCache.getRequest(request, response);
        String redirectUrl = savedRequest == null ? null : savedRequest.getRedirectUrl();

        // if the cached request address is null, then use the homepage
        redirectUrl = (redirectUrl == null || "".equals(redirectUrl)) ? homePage : redirectUrl;

        // if the homepage is null, then redirect to the root directory
        if (redirectUrl == null || "".equals(redirectUrl)) {
            redirectUrl = "";
        }

        // if the cached request is in the configured addresses, then change the redirect url to the homepage
        redirectUrl = existInIgnoreList(redirectUrl) ? homePage : redirectUrl;

        String returnType = request.getParameter(returnTypeParameterName);
        if (ReturnTypeEnum.JSON_TYPE.getValue().equalsIgnoreCase(returnType)) {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpStatus.OK.value());
            response.getWriter().write("{\"redirectUrl\": \"" + redirectUrl + "\"}");
        } else {
            redirectStrategy.sendRedirect(request, response, redirectUrl);
        }
    }

    private boolean existInIgnoreList(String redirectUrl) {

        if (ArrayUtils.isEmpty(ignorePaths)) {
            return false;
        }

        try {
            URL url = new URL(redirectUrl);
            String redirectPath = url.getPath();

            for (String path: ignorePaths) {
                if (pathMatcher.match(path, redirectPath)) {
                    return true;
                }
            }

        } catch (MalformedURLException e) {
            //
        }
        return false;
    }
}
