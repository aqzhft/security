package cc.powind.security.core.config;

import cc.powind.security.core.authentication.SmsCodeAuthenticationConfig;
import cc.powind.security.core.properties.SecurityProperties;
import cc.powind.security.core.validator.ValidateCode;
import cc.powind.security.core.validator.ValidateCodeFilter;
import cc.powind.security.core.validator.ValidateCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
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

        http.authorizeRequests()
                .mvcMatchers(noAuthUrlList.toArray(new String[0])).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginProcessingUrl(properties.getLoginProcessUrl())
                .loginPage(properties.getLoginPage())
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                .and()
                .rememberMe().key("powind").userDetailsService(userDetailsService)
                .tokenValiditySeconds(5 * 60)
                .and()
                .logout()
                .logoutSuccessUrl(properties.getLogoutUrl())
                .logoutSuccessHandler(logoutSuccessHandler)
                .and()
                .csrf().disable();

        http.apply(smsCodeAuthenticationConfig);
    }
}
