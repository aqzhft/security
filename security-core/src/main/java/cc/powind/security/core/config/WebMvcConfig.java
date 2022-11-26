package cc.powind.security.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired(required = false)
    private LoginIdArgumentResolver loginIdArgumentResolver;

    @Autowired(required = false)
    private LoginIdArgumentHeaderResolver loginIdArgumentHeaderResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {

        if (loginIdArgumentHeaderResolver != null) {
            resolvers.add(loginIdArgumentResolver);
        }

        if (loginIdArgumentResolver != null) {
            resolvers.add(loginIdArgumentResolver);
        }
    }

    public static class LoginIdArgumentResolver implements HandlerMethodArgumentResolver {

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return "loginId".equals(parameter.getParameterName());
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                return null;
            }

            Object principal = authentication.getPrincipal();

            if (principal instanceof User) {
                User user = (User) principal;
                return user.getUsername();
            }

            else if (principal instanceof OidcUser) {
                OidcUser userInfo = (OidcUser) principal;
                return userInfo.getName();
            }

            return null;
        }
    }

    public static class LoginIdArgumentHeaderResolver implements HandlerMethodArgumentResolver {

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return "loginId".equals(parameter.getParameterName());
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

            // 跟上游服务约定好loginId的参数名称
            HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
            if (request != null) {
                String loginId = request.getHeader("X-REQUEST-HEADER-LOGIN-ID");
                if (loginId != null) {
                    return URLDecoder.decode(loginId, StandardCharsets.UTF_8.displayName());
                }
            }

            return null;
        }
    }
}
