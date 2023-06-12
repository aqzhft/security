package cc.powind.security.basic.config;

import cc.powind.security.core.login.SecurityUserInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ContentVersionStrategy;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        VersionResourceResolver resourceResolver = new VersionResourceResolver()
                .addVersionStrategy(new ContentVersionStrategy(), "/**");

        registry.addResourceHandler("/statics/*", "/images/*")
                .addResourceLocations("classpath:/resources/statics/", "classpath:/resources/images/")
                .setCachePeriod(60 * 60 * 24 * 365)
                .resourceChain(true)
                .addResolver(resourceResolver);
    }

    /**
     * 这个可以让thymeleaf中的@{}生效
     */
    @Bean
    public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
        return new ResourceUrlEncodingFilter();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginInfoArgumentResolver());
    }

    class LoginInfoArgumentResolver implements HandlerMethodArgumentResolver {

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return "loginId".equals(parameter.getParameterName());
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() != null) {
                SecurityUserInfo userInfo = (SecurityUserInfo) authentication.getPrincipal();
                return userInfo.getLoginId();
            }

            return null;
        }
    }
}
