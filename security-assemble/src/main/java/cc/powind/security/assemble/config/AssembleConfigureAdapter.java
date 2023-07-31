package cc.powind.security.assemble.config;

import cc.powind.security.assemble.properties.SecurityProperties;
import cc.powind.security.core.authorize.RbacService;
import cc.powind.security.core.login.LoginInfo;
import cc.powind.security.core.login.LoginInfoService;
import cc.powind.security.core.login.SecurityUserInfo;
import cc.powind.security.token.model.Token;
import cc.powind.security.token.model.VerifyToken;
import cc.powind.security.token.service.TokenNotifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ContentVersionStrategy;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.*;

@Import({ValidateCodeConfig.class, LoginConfig.class})
@EnableConfigurationProperties(SecurityProperties.class)
public class AssembleConfigureAdapter implements AssembleConfigure, WebMvcConfigurer {

    @Autowired
    private SecurityProperties properties;

    private final Set<AntPathRequestMatcher> noCheckPathList = new HashSet<>();

    @PostConstruct
    public void init() {

        // 重置密码不需要权限校验
        noCheckPathList.add(new AntPathRequestMatcher("/password", HttpMethod.POST.name()));
        noCheckPathList.add(new AntPathRequestMatcher("/password/reset", HttpMethod.POST.name()));
        noCheckPathList.add(new AntPathRequestMatcher("/permission"));

        for (String noCheckPath : properties.getNoCheckPath()) {

            String[] split = noCheckPath.split(":");
            if (split.length == 2) {
                HttpMethod method = HttpMethod.resolve(split[0]);
                Assert.notNull(method, "security properties noCheckPath method config error: " + split[1]);
                noCheckPathList.add(new AntPathRequestMatcher(split[0], method.name()));
            }
        }
    }

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

    static class LoginInfoArgumentResolver implements HandlerMethodArgumentResolver {

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

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

                Map<String, LoginInfo> mappings = Optional.of(loginInfoMappings()).orElse(Collections.emptyMap());

                LoginInfo loginInfo = mappings.get(username);
                if (loginInfo != null) {
                    return loginInfo.getUserInfo();
                }

                throw new UsernameNotFoundException("未查询到【" + username + "】相关的用户");
            }
        };
    }

    @Bean
    @Override
    public LoginInfoService loginInfoService() {

        return new LoginInfoService() {
            @Override
            public SecurityUserInfo load(String identifyId, Type type) {

                Map<String, LoginInfo> mappings = Optional.of(loginInfoMappings()).orElse(Collections.emptyMap());

                LoginInfo loginInfo = mappings.get(identifyId);
                if (loginInfo != null) {
                    if (type == null || type.equals(loginInfo.getType())) {
                        return loginInfo.getUserInfo();
                    }
                }

                return null;
            }

            @Override
            public void updatePassword(String loginId, String encodePassword) {
                doUpdatePassword(loginId, encodePassword);
            }
        };
    }

    @Bean
    @Override
    public RbacService rbacService() {
        return (request, authentication) -> {

            // 请求路径
            String uri = request.getRequestURI();

            // 请求方法
            String method = request.getMethod();

            // 如果是修改密码的不要拦截
            for (AntPathRequestMatcher matcher : noCheckPathList) {
                if (matcher.matches(request)) {
                    return true;
                }
            }

            // 需要一个获取权限的接口
            return checkPermission(uri, method);
        };
    }

    protected boolean checkPermission(String uri, String method) {
        return true;
    }

    protected Map<String, LoginInfo> loginInfoMappings() {
        return null;
    }

    protected void doUpdatePassword(String loginId, String encodePassword) {}

    @Bean
    public TokenNotifier tokenNotifier() {
        return new TokenNotifier() {
            @Override
            public void send(Token token, HttpServletRequest request, HttpServletResponse response) throws IOException {
                System.out.println("token =>" + token + "新的notifier");

                if (token instanceof VerifyToken) {
                    System.out.println("verify token ---> ");
                }
            }
        };
    }

    @Bean("authorizeKey")
    @Override
    public KeyPair authorizeKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    @Bean
    @Override
    public RegisteredClientRepository authorizationClientRepository() {
        return new InMemoryRegisteredClientRepository();
    }
}
