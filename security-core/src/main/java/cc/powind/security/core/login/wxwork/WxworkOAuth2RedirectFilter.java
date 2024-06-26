package cc.powind.security.core.login.wxwork;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class WxworkOAuth2RedirectFilter extends OncePerRequestFilter {

    private static final String DEFAULT_WXWORK_PROCESS_URI = "/wxwork/oauth2/authorization";

    private static final String LINK = "link", CODE = "code";

    private static final String LINK_REDIRECT_ADDRESS = "%s?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";

    private static final String CODE_REDIRECT_ADDRESS = "%s?appid=%s&agentid=%s&redirect_uri=%s&state=STATE";

    private static final String DEFAULT_AUTHORIZATION_URI = "https://open.weixin.qq.com/connect/oauth2/authorize";

    private static final String DEFAULT_AUTHORIZATION_QRCODE_URI = "https://open.work.weixin.qq.com/wwopen/sso/qrConnect";

    private final PathMatcher pathMatcher = new AntPathMatcher();

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private String corpId;

    private String agentId;

    private String authorizationUri = DEFAULT_AUTHORIZATION_URI;

    private String authorizationQrcodeUri = DEFAULT_AUTHORIZATION_QRCODE_URI;

    private String redirectUri;

    private final Map<String, AgentInfo> agentInfoMap = new HashMap<>(16);

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAuthorizationUri() {
        return authorizationUri;
    }

    public void setAuthorizationUri(String authorizationUri) {
        this.authorizationUri = authorizationUri;
    }

    public void setAuthorizationUriUsingDefaultIfNull(String authorizationUri) {
        this.authorizationUri = StringUtils.isBlank(authorizationUri) ? DEFAULT_AUTHORIZATION_URI : authorizationUri;
    }

    public String getAuthorizationQrcodeUri() {
        return authorizationQrcodeUri;
    }

    public void setAuthorizationQrcodeUri(String authorizationQrcodeUri) {
        this.authorizationQrcodeUri = authorizationQrcodeUri;
    }

    public void setAuthorizationQrcodeUriUsingDefaultIfNull(String authorizationQrcodeUri) {
        this.authorizationQrcodeUri = StringUtils.isBlank(authorizationQrcodeUri) ? DEFAULT_AUTHORIZATION_QRCODE_URI : authorizationQrcodeUri;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public void addAgentInfo(String agentId, String secret, String authorizationUri, String redirectUri) {
        agentInfoMap.put(agentId, new AgentInfo(agentId, secret, authorizationUri, redirectUri));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Determine if it is the address that WeCom needs to intercept
        if (checkIsWxworkRedirectURI(request)) {

            // If parameter agentId is not null, execute new build logic
            String requestAgentId = request.getParameter("agentId");
            String redirectURI = buildRedirectURI(requestAgentId);
            if (redirectURI != null) {
                redirectStrategy.sendRedirect(request, response, redirectURI);
                return;
            }

            String type = request.getParameter("type");

            // Build redirect address
            String targetURI = getTargetURI(type);
            Assert.notNull(targetURI, "Redirect address is null");

            // Actual redirection
            redirectStrategy.sendRedirect(request, response, targetURI);

            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean checkIsWxworkRedirectURI(HttpServletRequest request) {
        return pathMatcher.match(DEFAULT_WXWORK_PROCESS_URI, request.getRequestURI());
    }

    /**
     * https://open.weixin.qq.com/connect/oauth2/authorize?appid=CORPID&redirect_uri=REDIRECT_URI&response_type=code&scope=snsapi_base&state=STATE&agentid=AGENTID#wechat_redirect
     * https://open.work.weixin.qq.com/wwopen/sso/qrConnect?appid=CORPID&agentid=AGENTID&redirect_uri=REDIRECT_URI&state=STATE
     */
    private String getTargetURI(String type) throws UnsupportedEncodingException {

        String redirectURI = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.name());

        if (type == null || LINK.equals(type)) {
            return String.format(LINK_REDIRECT_ADDRESS, authorizationUri, corpId, redirectURI);
        }

        else if (CODE.equals(type)) {
            return String.format(CODE_REDIRECT_ADDRESS, authorizationQrcodeUri, corpId, agentId, redirectURI);
        }

        return null;
    }

    private String buildRedirectURI(String agentId) {

        AgentInfo agentInfo = getAgentInfo(agentId);
        if (agentInfo == null) {
            logger.warn("Can't get agent information when use agent id: " + agentId);
            return null;
        }

        Assert.notNull(corpId, "Corp id is null in wxwork authentication");

        return agentInfo.generateURI(corpId);
    }

    private AgentInfo getAgentInfo(String agentId) {
        return agentInfoMap.get(agentId);
    }
}
