package cc.powind.security.core.login.verify;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;

public class VerifyCodeAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getName());
        return new VerifyCodeAuthenticationToken(userDetails.getUsername(), Collections.emptyList());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return VerifyCodeAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
