package cc.powind.security.core.login.wxwork;

import cc.powind.security.core.properties.WxworkProperties;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.util.Assert;
import org.springframework.web.client.RestOperations;

import java.net.URI;

public class WxworkAuthenticationProvider implements AuthenticationProvider {

    private static final String INVALID_STATE_PARAMETER_ERROR_CODE = "invalid_state_parameter";

    private static final String INVALID_ID_TOKEN_ERROR_CODE = "invalid_id_token";

    private static final String INVALID_NONCE_ERROR_CODE = "invalid_nonce";

    private RestOperations restOperations;

    private WxworkProperties properties;

    public RestOperations getRestOperations() {
        return restOperations;
    }

    public void setRestOperations(RestOperations restOperations) {
        this.restOperations = restOperations;
    }

    public WxworkProperties getProperties() {
        return properties;
    }

    public void setProperties(WxworkProperties properties) {
        this.properties = properties;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        WxworkAuthenticationToken token = (WxworkAuthenticationToken) authentication;

        UserInfoResponse userInfoResponse = getResponse(token);
        Assert.notNull(userInfoResponse, "wxwork user info not found");

        token.setUserId(userInfoResponse.getUserid());

        token.setAuthenticated(true);

        return token;
    }

    /**
     * 根据token（包括回调方法携带的 code）
     * 还需要一个access_token
     */
    private UserInfoResponse getResponse(WxworkAuthenticationToken token) {
        try {

            String accessToken = getAccessToken(properties.getCorpId(), properties.getCorpSecret());
            Assert.notNull(accessToken, "get wxwork access token error");

            String uri = properties.getUserInfoUri() + "?access_token=%s&code=%s";

            ResponseEntity<UserInfoResponse> exchange = restOperations.exchange(new RequestEntity<>(HttpMethod.GET, URI.create(String.format(uri, accessToken, token.getCode()))), UserInfoResponse.class);

            if (exchange.getStatusCode() == HttpStatus.OK) {
                return exchange.getBody() == null ? null : exchange.getBody();
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

    private String getAccessToken(String corpId, String corpSecret) {

        String uri = properties.getTokenUri() + "?corpid=%s&corpsecret=%s";

        ResponseEntity<AccessTokenResponse> responseEntity = restOperations.exchange(new RequestEntity<>(HttpMethod.GET, URI.create(String.format(uri, corpId, corpSecret))), AccessTokenResponse.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody() == null ? null : responseEntity.getBody().access_token;
        }

        return null;
    }

    static class AccessTokenResponse {

        private int errcode;

        private String errmsg;

        private String access_token;

        private int expires_in;

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
