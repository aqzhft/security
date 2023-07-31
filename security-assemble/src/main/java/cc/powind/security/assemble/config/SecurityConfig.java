package cc.powind.security.assemble.config;

import cc.powind.security.assemble.properties.SecurityProperties;
import cc.powind.security.core.authorize.RbacService;
import cc.powind.security.core.exception.ValidateCodeException;
import cc.powind.security.core.login.LoginInfoService;
import cc.powind.security.core.login.SecurityEntrypoint;
import cc.powind.security.core.login.SecurityUserInfo;
import cc.powind.security.core.login.sms.SmsCodeAuthenticationConfig;
import cc.powind.security.core.login.verify.VerifyCodeAuthenticationConfig;
import cc.powind.security.core.login.wxwork.WxworkAuthenticationConfig;
import cc.powind.security.core.proxy.RequestParameterEnum;
import cc.powind.security.token.TokenIntercept;
import cc.powind.security.token.exception.TokenInvalidException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final Set<String> noAuthUrlList = new HashSet<>();

    @Autowired
    private SecurityProperties properties;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private RbacService rbacService;

    @Autowired
    @Qualifier("authenticationSuccessHandler")
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    @Qualifier("oauth2SuccessHandler")
    private AuthenticationSuccessHandler oauth2SuccessHandler;

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private LogoutSuccessHandler logoutSuccessHandler;

    @Autowired
    private SmsCodeAuthenticationConfig smsCodeAuthenticationConfig;

    @Autowired
    private WxworkAuthenticationConfig wxworkAuthenticationConfig;

    @Autowired
    private VerifyCodeAuthenticationConfig verifyCodeAuthenticationConfig;

    @Autowired
    private TokenIntercept tokenIntercept;

    @Autowired(required = false)
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoginInfoService loginInfoService;

    private final RequestCache requestCache = new CustomRequestCache();

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @PostConstruct
    public void initNotAuthUrlList() {
        noAuthUrlList.add(properties.getPath().getLoginPage());
        noAuthUrlList.add(properties.getPath().getFormLoginUrl());
        noAuthUrlList.add("/code/{type}");
        noAuthUrlList.add("/statics/**");
        noAuthUrlList.add("/images/**");

        if (ArrayUtils.isNotEmpty(properties.getNoAuthPath())) {
            noAuthUrlList.addAll(Arrays.asList(properties.getNoAuthPath()));
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.addFilterBefore(new TokenInterceptFilter(), UsernamePasswordAuthenticationFilter.class);

        http.authorizeRequests(authorize -> {
            authorize.mvcMatchers(noAuthUrlList.toArray(new String[0])).permitAll();
            authorize.mvcMatchers("/**").access("@rbacService.hasPermission(request, authentication) and isAuthenticated()");
        });

        http.formLogin(form -> {
            form.loginProcessingUrl(properties.getPath().getFormLoginUrl());
            form.loginPage(properties.getPath().getBasePath() + properties.getPath().getLoginPage());
            form.successHandler(authenticationSuccessHandler);
            form.failureHandler(authenticationFailureHandler);
        });

        http.sessionManagement(session -> {
            session.maximumSessions(properties.getSession().getMaximumSessions());
            session.invalidSessionUrl(properties.getPath().getBasePath() + properties.getPath().getLoginPage());
            session.invalidSessionStrategy(new InvalidSessionStrategyImpl());
        });

        http.rememberMe(rememberMe -> {
            rememberMe.key(properties.getRememberMe().getKey());
            rememberMe.tokenValiditySeconds(properties.getRememberMe().getTokenValiditySeconds());
            rememberMe.userDetailsService(userDetailsService);
        });

        http.logout(logout -> {
            logout.logoutSuccessHandler(logoutSuccessHandler);
            logout.logoutSuccessUrl(properties.getPath().getLoginPage());
            logout.logoutUrl(properties.getPath().getLogoutUrl());
            logout.deleteCookies("JSESSIONID");
        });

        http.requestCache(cache -> {
            ((CustomRequestCache) requestCache).initRequestMatcher(http);
            cache.requestCache(requestCache);
        });

        http.csrf().disable();

        http.userDetailsService(userDetailsService);

        http.apply(smsCodeAuthenticationConfig);
        http.apply(wxworkAuthenticationConfig);
        http.apply(verifyCodeAuthenticationConfig);

        if (clientRegistrationRepository != null) {
            http.oauth2Login(oauth2Login -> {
                oauth2Login.successHandler(oauth2SuccessHandler);
                oauth2Login.failureHandler(authenticationFailureHandler);
            });
        }

        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();
        http.apply(authorizationServerConfigurer);
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .authorizationEndpoint(endpoint -> endpoint.consentPage("/consent"))
                .oidc(Customizer.withDefaults());	// Enable OpenID Connect 1.0

        http.oauth2ResourceServer((resourceServer) -> resourceServer.jwt(Customizer.withDefaults()));

        http.exceptionHandling().authenticationEntryPoint(new MyAuthenticationEntryPoint(properties.getPath().getBasePath() + properties.getPath().getLoginPage()));
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(@Autowired @Qualifier("authorizeKey") KeyPair keyPair) {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public SecurityEntrypoint securityEntrypoint() {
        SecurityEntrypoint entrypoint = new SecurityEntrypoint();
        entrypoint.setPath(properties.getPath());
        entrypoint.setPage(properties.getPage());
        entrypoint.setWxworkProperties(properties.getWxwork());
        entrypoint.setRepository(clientRegistrationRepository);
        entrypoint.setShowLoginWays(properties.getLoginWays());
        entrypoint.setRbacService(rbacService);
        entrypoint.setLoginInfoService(loginInfoService);
        entrypoint.setPasswordEncoder(passwordEncoder);
        return entrypoint;
    }

    @Bean
    public OidcUserService oidcUserService(LoginInfoService loginInfoService) {
        return new OAuth2UserServiceImpl(loginInfoService);
    }

    class TokenInterceptFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

            try {

                tokenIntercept.intercept(request, response);

                filterChain.doFilter(request, response);
            } catch (TokenInvalidException e) {

                authenticationFailureHandler.onAuthenticationFailure(request, response, new ValidateCodeException(e.getMessage()));
            }
        }
    }

    class MyAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

        /**
         * @param loginFormUrl URL where the login page can be found. Should either be
         *                     relative to the web-app context path (include a leading {@code /}) or an absolute
         *                     URL.
         */
        public MyAuthenticationEntryPoint(String loginFormUrl) {
            super(loginFormUrl);
        }

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

            if (checkIfNginxAuthRequestCheck(request)) {

                if (authException instanceof InsufficientAuthenticationException) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                }

            } else {
                super.commence(request, response, authException);
            }
        }
    }

    class InvalidSessionStrategyImpl implements InvalidSessionStrategy {

        @Override
        public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

            request.getSession();

            if (checkIfNginxAuthRequestCheck(request)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
            } else {
                redirectStrategy.sendRedirect(request, response, properties.getPath().getBasePath() + properties.getPath().getLoginPage());
            }
        }
    }

    class OAuth2UserServiceImpl extends OidcUserService {

        private final LoginInfoService loginInfoService;

        public OAuth2UserServiceImpl(LoginInfoService loginInfoService) {
            this.loginInfoService = loginInfoService;
        }

        @Override
        public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {

            OidcUser oidcUser = super.loadUser(userRequest);

            SecurityUserInfo userInfo  = loginInfoService.load(oidcUser.getName(), LoginInfoService.Type.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase()));

            if (userInfo != null) {
                return userInfo;
            }

            return buildDefaultOidcUserInfo(oidcUser, userRequest);
        }
    }

    private SecurityUserInfo buildDefaultOidcUserInfo(OidcUser user, OidcUserRequest userRequest) {
        SecurityUserInfo userInfo = new SecurityUserInfo();
        userInfo.setLoginId(userRequest.getClientRegistration().getRegistrationId() + "_" + user.getName());
        userInfo.setUsername(user.getName());
        userInfo.setEmail(user.getEmail());
        return userInfo;
    }

    /**
     * 判断是不是NGINX中auth_request模块校验权限的接口
     */
    private boolean checkIfNginxAuthRequestCheck(HttpServletRequest request) {
        String headerNginx = request.getHeader(RequestParameterEnum.NGINX_AUTH.getValue());
        return  "1".equals(headerNginx);
    }
}
