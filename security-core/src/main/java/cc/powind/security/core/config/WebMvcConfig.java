package cc.powind.security.core.config;

import cc.powind.security.core.login.LoginIdService;
import cc.powind.security.core.login.LoginInfo;
import cc.powind.security.core.login.sms.SmsCodeAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    public static final String LOGIN_ID = "loginId";

    public static final String REQUEST_HEADER_LOGIN_ID = "X-REQUEST-HEADER-LOGIN-ID";

    @Autowired
    private LoginIdService loginIdService;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginIdArgumentResolver());
    }

    public class LoginIdArgumentResolver implements HandlerMethodArgumentResolver {

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return LOGIN_ID.equals(parameter.getParameterName());
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
            return getLoginId(webRequest);
        }
    }

    /**
     * 1、查询本地securityContext有没有loginId
     * 2、上游的header中是否存在loginId
     * 3、都没有就返回null
     *
     * @param webRequest request & response
     * @return loginId
     * @throws UnsupportedEncodingException urlDecoder
     */
    String getLoginId(NativeWebRequest webRequest) throws UnsupportedEncodingException {

        Identify identify = getIdentify(SecurityContextHolder.getContext().getAuthentication());
        if (identify != null) {
            LoginInfo loginInfo = loginIdService.load(identify.getUsername(), identify.getType());
            if (loginInfo != null) {
                return loginInfo.getLoginId();
            }

            return identify.getType() + "_" + identify.getUsername();
        }

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request != null) {
            String loginId = request.getHeader(REQUEST_HEADER_LOGIN_ID);
            if (loginId != null) {
                return URLDecoder.decode(loginId, StandardCharsets.UTF_8.displayName());
            }
        }

        return null;
    }

    private Identify getIdentify(Authentication authentication) {
        if (authentication != null) {
            if (authentication instanceof OAuth2AuthenticationToken) {

                OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

                OAuth2User userInfo = token.getPrincipal();

                return new Identify(userInfo.getName(), token.getAuthorizedClientRegistrationId());

            } else if (authentication instanceof UsernamePasswordAuthenticationToken) {
                User user = (User) authentication.getPrincipal();
                return new Identify(user.getUsername(), "username");
            } else if (authentication instanceof SmsCodeAuthenticationToken) {
                return new Identify((String) authentication.getPrincipal(), "mobile");
            }
        }
        return null;
    }

    class Identify {

        private final String username;

        private final String type;

        public Identify(String username, String type) {
            this.username = username;
            this.type = type;
        }

        public String getUsername() {
            return username;
        }

        public String getType() {
            return type;
        }
    }
}
