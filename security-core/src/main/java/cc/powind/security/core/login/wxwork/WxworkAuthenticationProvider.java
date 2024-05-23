package cc.powind.security.core.login.wxwork;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.util.Assert;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class WxworkAuthenticationProvider implements AuthenticationProvider {

    private final Log log = LogFactory.getLog(getClass());

    private static final String INVALID_STATE_PARAMETER_ERROR_CODE = "invalid_state_parameter";

    private static final String INVALID_ID_TOKEN_ERROR_CODE = "invalid_id_token";

    private static final String INVALID_NONCE_ERROR_CODE = "invalid_nonce";

    private static final String DEFAULT_TOKEN_URI = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";

    private static final String DEFAULT_USER_INFO_URI = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";

    private String corpId;

    private String corpSecret;

    private String userInfoUri = DEFAULT_USER_INFO_URI;

    private String tokenUri = DEFAULT_TOKEN_URI;

    private RestOperations restOperations;

    private UserDetailsService userDetailsService;

    private final Map<String, AgentInfo> agentInfoMap = new HashMap<>(16);

    private final Map<String, AccessTokenResponse> tokenCache = Collections.synchronizedMap(new LinkedHashMap<String, AccessTokenResponse>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, AccessTokenResponse> eldest) {
            return size() > 16;
        }
    });

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getCorpSecret() {
        return corpSecret;
    }

    public void setCorpSecret(String corpSecret) {
        this.corpSecret = corpSecret;
    }

    public String getUserInfoUri() {
        return userInfoUri;
    }

    public void setUserInfoUri(String userInfoUri) {
        this.userInfoUri = userInfoUri;
    }

    public void setUserInfoUriUsingDefaultIfNull(String userInfoUri) {
        this.userInfoUri = StringUtils.isBlank(userInfoUri) ? DEFAULT_USER_INFO_URI : userInfoUri;
    }

    public String getTokenUri() {
        return tokenUri;
    }

    public void setTokenUri(String tokenUri) {
        this.tokenUri = tokenUri;
    }

    public void setTokenUriUsingDefaultIfNull(String tokenUri) {
        this.tokenUri = StringUtils.isBlank(tokenUri) ? DEFAULT_TOKEN_URI : tokenUri;
    }

    public RestOperations getRestOperations() {
        return restOperations;
    }

    public void setRestOperations(RestOperations restOperations) {
        this.restOperations = restOperations;
    }

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public void addAgentInfo(String agentId, String secret, String authorizationUri, String redirectUri) {
        agentInfoMap.put(agentId, new AgentInfo(agentId, secret, authorizationUri, redirectUri));
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        WxworkAuthenticationToken token = (WxworkAuthenticationToken) authentication;

        UserInfoResponse userInfoResponse = getResponse(token);
        Assert.notNull(userInfoResponse, "Wxwork user info not found");

        UserDetails userDetails = userDetailsService.loadUserByUsername(userInfoResponse.getUserid());
        token.setPrincipal(userDetails);

        token.setAuthenticated(true);

        return token;
    }

    private UserInfoResponse getResponse(WxworkAuthenticationToken token) {
        try {

            // There is a very disgusting question here
            // Obtaining a token requires two parameters: corpId and corpSecret.
            // The corpId is fixed, but the corpSecret is different for each application,
            // and there is no information in the callback address to determine which application initiated the login request
            // At present, the agentId parameter can only be forcibly added to the callback address
            String accessToken = getAccessToken(corpId, token.getAgentId(), corpSecret);
            Assert.notNull(accessToken, "Get wxwork access token error");

            String uri = userInfoUri + "?access_token=%s&code=%s";

            ResponseEntity<UserInfoResponse> exchange = restOperations.exchange(new RequestEntity<>(HttpMethod.GET, URI.create(String.format(uri, accessToken, token.getCode()))), UserInfoResponse.class);

            if (exchange.getStatusCode() == HttpStatus.OK) {
                if (exchange.getBody() != null) {
                    UserInfoResponse response = exchange.getBody();
                    if (response.getErrcode() != 0) {
                        log.error(response.getErrmsg());
                    } else {
                        return response;
                    }
                }
            }

            return null;
        }
        catch (OAuth2AuthorizationException ex) {
            OAuth2Error oauth2Error = ex.getError();
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return WxworkAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private String getAccessToken(String corpId, String agentId, String corpSecret) {

        AgentInfo agentInfo = agentInfoMap.get(agentId);
        if (agentInfo != null) {
            corpSecret = agentInfo.getSecret();
        }

        final String finalSecret = corpSecret;
        final String cacheKey = corpId + "_" + corpSecret;
        AccessTokenResponse tokenResponse = tokenCache.get(cacheKey);
        if (tokenResponse != null && tokenResponse.isValid()) {
            return tokenResponse.access_token;
        }

        tokenResponse = doGetAccessToken(corpId, finalSecret);
        if (tokenResponse == null) {
            return null;
        }

        tokenCache.put(cacheKey, tokenResponse);
        return tokenResponse.access_token;
    }

    private AccessTokenResponse doGetAccessToken(String corpId, String corpSecret) {

        String uri = tokenUri + "?corpid=%s&corpsecret=%s";

        ResponseEntity<AccessTokenResponse> responseEntity = restOperations.exchange(new RequestEntity<>(HttpMethod.GET, URI.create(String.format(uri, corpId, corpSecret))), AccessTokenResponse.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            if (responseEntity.getBody() != null) {
                AccessTokenResponse response = responseEntity.getBody();
                if (response.getErrcode() != 0) {
                    log.error(response.getErrmsg());
                } else {
                    // Set create time for determine when expired
                    response.setCreateTime(Instant.now());
                    return response;
                }
            }
        }

        return null;
    }

    static class AccessTokenResponse {

        private int errcode;

        private String errmsg;

        private String access_token;

        private int expires_in;

        private Instant createTime;

        public int getErrcode() {
            return errcode;
        }

        public void setErrcode(int errcode) {
            this.errcode = errcode;
        }

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public int getExpires_in() {
            return expires_in;
        }

        public void setExpires_in(int expires_in) {
            this.expires_in = expires_in;
        }

        public Instant getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Instant createTime) {
            this.createTime = createTime;
        }

        public boolean isValid() {
            return Instant.now().isBefore(createTime.plusSeconds(expires_in - 600));
        }
    }

    static class UserInfoResponse {

        private int errcode;

        private String errmsg;

        private String userid;

        public int getErrcode() {
            return errcode;
        }

        public void setErrcode(int errcode) {
            this.errcode = errcode;
        }

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }
    }
}
