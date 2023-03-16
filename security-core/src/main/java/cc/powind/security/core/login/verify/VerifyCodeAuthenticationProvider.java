package cc.powind.security.core.login.verify;

import cc.powind.security.core.login.LoginInfoService;
import cc.powind.security.core.login.SecurityUserInfo;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

public class VerifyCodeAuthenticationProvider implements AuthenticationProvider {

    private LoginInfoService loginInfoService;

    public void setUserDetailsService(LoginInfoService loginInfoService) {
        this.loginInfoService = loginInfoService;
    }

    @Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        VerifyCodeAuthenticationToken authenticationToken = (VerifyCodeAuthenticationToken) authentication;

        SecurityUserInfo userInfo = loginInfoService.load(authenticationToken.getVerifyCode());

        if (userInfo != null) {
            return new VerifyCodeAuthenticationToken(userInfo, Collections.emptyList());
        }

        throw new UsernameNotFoundException("未查询到【" + authentication.getName() + "】相关的信息");
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return VerifyCodeAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
