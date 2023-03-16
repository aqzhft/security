package cc.powind.security.core.config;

import cc.powind.security.core.login.LoginInfoService;
import cc.powind.security.core.login.SecurityUserInfo;
import cc.powind.security.core.login.sms.SmsCodeAuthenticationConfig;
import cc.powind.security.core.login.verify.VerifyCodeAuthenticationConfig;
import cc.powind.security.core.login.wxwork.WxworkAuthenticationConfig;
import cc.powind.security.core.login.wxwork.WxworkOAuth2RedirectFilter;
import cc.powind.security.core.properties.SecurityProperties;
import cc.powind.security.core.validator.ValidateCode;
import cc.powind.security.core.validator.ValidateCodeFilter;
import cc.powind.security.core.validator.ValidateCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.session.InvalidSessionStrategy;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@Import({ValidateCodeConfig.class, LoginConfig.class})
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityProperties properties;

    private final Set<String> noAuthUrlList = new HashSet<>();

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

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

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @PostConstruct
    public void initNotAuthUrlList() {
        noAuthUrlList.add(properties.getPath().getLoginPage());
        noAuthUrlList.add(properties.getPath().getFormLoginUrl());
        noAuthUrlList.add("/code/{type}");
        noAuthUrlList.add("/statics/**");
        noAuthUrlList.add("/images/**");
    }

    @Autowired
    private List<ValidateCodeService<? extends ValidateCode>> validateCodeServices;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        ValidateCodeFilter validateCodeFilter = new ValidateCodeFilter();
        validateCodeFilter.setValidateCodeServices(validateCodeServices);
        validateCodeFilter.setAuthenticationFailureHandler(authenticationFailureHandler);

        http.addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class);

        WxworkOAuth2RedirectFilter wxworkOAuth2RedirectFilter = new WxworkOAuth2RedirectFilter();
        wxworkOAuth2RedirectFilter.setProperties(properties.getWxwork());
        http.addFilterBefore(wxworkOAuth2RedirectFilter, OAuth2LoginAuthenticationFilter.class);

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

        http.csrf().disable();

        http.userDetailsService(userDetailsService);

        http.apply(smsCodeAuthenticationConfig);
        http.apply(wxworkAuthenticationConfig);
        http.apply(verifyCodeAuthenticationConfig);

        http.oauth2Login(oauth2Login -> {
            oauth2Login.successHandler(authenticationSuccessHandler);
            oauth2Login.failureHandler(authenticationFailureHandler);
        });

        http.exceptionHandling().authenticationEntryPoint(new MyAuthenticationEntryPoint(properties.getPath().getBasePath() + properties.getPath().getLoginPage()));
    }

    @Bean
    public OidcUserService oidcUserService(LoginInfoService loginInfoService) {
        return new OAuth2UserServiceImpl(loginInfoService);
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
        String headerNginx = request.getHeader("X-NGINX-AUTH");
        return  "1".equals(headerNginx);
    }
}
