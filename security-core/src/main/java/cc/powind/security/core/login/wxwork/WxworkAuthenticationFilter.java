package cc.powind.security.core.login.wxwork;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * {@link OAuth2LoginAuthenticationFilter}
 */
public class WxworkAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String DEFAULT_WXWORK_REGISTRATION_ID = "wxwork";

    public static final String DEFAULT_FILTER_PROCESSES_URI = "/wxwork/login/oauth2/code";

    private static final String AUTHORIZATION_REQUEST_NOT_FOUND_ERROR_CODE = "authorization_request_not_found";

    private static final String CLIENT_REGISTRATION_NOT_FOUND_ERROR_CODE = "client_registration_not_found";

    public WxworkAuthenticationFilter() {
        super(DEFAULT_FILTER_PROCESSES_URI);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String code = request.getParameter("code");
        String agentId = request.getParameter("agentId");

        WxworkAuthenticationToken authenticationRequest = new WxworkAuthenticationToken(code, agentId);

        setDetails(request, authenticationRequest);

        return this.getAuthenticationManager().authenticate(authenticationRequest);
    }

    protected void setDetails(HttpServletRequest request, WxworkAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }
}
