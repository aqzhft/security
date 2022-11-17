package cc.powind.security.core.config;

import cc.powind.security.core.login.sms.SmsCodeAuthenticationConfig;
import cc.powind.security.core.login.wxwork.WxworkAuthenticationConfig;
import cc.powind.security.core.login.wxwork.WxworkOAuth2RedirectFilter;
import cc.powind.security.core.properties.SecurityProperties;
import cc.powind.security.core.validator.ValidateCode;
import cc.powind.security.core.validator.ValidateCodeFilter;
import cc.powind.security.core.validator.ValidateCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.annotation.PostConstruct;
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

    @PostConstruct
    public void initNotAuthUrlList() {
        noAuthUrlList.add(properties.getLoginPage());
        noAuthUrlList.add(properties.getLoginProcessUrl());
        noAuthUrlList.add("/code/{type}");
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
            form.loginProcessingUrl(properties.getLoginProcessUrl());
            form.loginPage(properties.getLoginPage());
            form.successHandler(authenticationSuccessHandler);
            form.failureHandler(authenticationFailureHandler);
        });

        http.sessionManagement(session -> {
            session.maximumSessions(properties.getSession().getMaximumSessions());
            session.invalidSessionUrl(properties.getLoginPage());
        });

        http.rememberMe(rememberMe -> {
            rememberMe.key(properties.getRememberMe().getKey());
            rememberMe.tokenValiditySeconds(properties.getRememberMe().getTokenValiditySeconds());
        });

        http.logout(logout -> {
            logout.logoutSuccessHandler(logoutSuccessHandler);
            logout.logoutSuccessUrl(properties.getLoginPage());
            logout.logoutUrl(properties.getLogoutUrl());
            logout.deleteCookies("JSESSIONID");
        });

        http.csrf().disable();

        http.userDetailsService(userDetailsService);

        http.apply(smsCodeAuthenticationConfig);
        http.apply(wxworkAuthenticationConfig);

        http.oauth2Login();
    }
}
