package cc.powind.security.core.login.verify;

import cc.powind.security.core.login.LoginInfoService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class VerifyCodeAuthenticationConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	private AuthenticationSuccessHandler authenticationSuccessHandler;

	private AuthenticationFailureHandler authenticationFailureHandler;

    private LoginInfoService loginInfoService;

    private String pattern;

    public AuthenticationSuccessHandler getAuthenticationSuccessHandler() {
        return authenticationSuccessHandler;
    }

    public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    public AuthenticationFailureHandler getAuthenticationFailureHandler() {
        return authenticationFailureHandler;
    }

    public void setAuthenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    public LoginInfoService getLoginInfoService() {
        return loginInfoService;
    }

    public void setLoginInfoService(LoginInfoService loginInfoService) {
        this.loginInfoService = loginInfoService;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
	public void configure(HttpSecurity http) throws Exception {

		VerifyCodeAuthenticationFilter verifyCodeAuthenticationFilter = new VerifyCodeAuthenticationFilter(pattern);
        verifyCodeAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        verifyCodeAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        verifyCodeAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);

        RememberMeServices rememberMeServices = http.getSharedObject(RememberMeServices.class);
        if (rememberMeServices != null) {
            verifyCodeAuthenticationFilter.setRememberMeServices(rememberMeServices);
        }

        VerifyCodeAuthenticationProvider verifyCodeAuthenticationProvider = new VerifyCodeAuthenticationProvider();
        verifyCodeAuthenticationProvider.setUserDetailsService(loginInfoService);

		http.authenticationProvider(verifyCodeAuthenticationProvider)
			.addFilterAfter(verifyCodeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
	}
}
