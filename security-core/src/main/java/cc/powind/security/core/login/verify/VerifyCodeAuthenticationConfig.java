package cc.powind.security.core.login.verify;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class VerifyCodeAuthenticationConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	private AuthenticationSuccessHandler authenticationSuccessHandler;

	private AuthenticationFailureHandler authenticationFailureHandler;

    private UserDetailsService userDetailsService;

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

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
	public void configure(HttpSecurity http) throws Exception {

		VerifyCodeAuthenticationFilter verifyCodeAuthenticationFilter = new VerifyCodeAuthenticationFilter();
        verifyCodeAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        verifyCodeAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        verifyCodeAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);

        VerifyCodeAuthenticationProvider verifyCodeAuthenticationProvider = new VerifyCodeAuthenticationProvider();
        verifyCodeAuthenticationProvider.setUserDetailsService(userDetailsService);

		http.authenticationProvider(verifyCodeAuthenticationProvider)
			.addFilterAfter(verifyCodeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
	}
}