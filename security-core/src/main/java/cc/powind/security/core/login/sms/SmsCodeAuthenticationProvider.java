package cc.powind.security.core.login.sms;

import cc.powind.security.core.login.LoginInfoService;
import cc.powind.security.core.login.SecurityUserInfo;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Collections;

public class SmsCodeAuthenticationProvider implements AuthenticationProvider {

    private LoginInfoService loginInfoService;

    public LoginInfoService getLoginInfoService() {
        return loginInfoService;
    }

    public void setLoginInfoService(LoginInfoService loginInfoService) {
        this.loginInfoService = loginInfoService;
    }

    @Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken) authentication;

        SecurityUserInfo userInfo = loginInfoService.load(authenticationToken.getMobile(), LoginInfoService.Type.MOBILE);

        if (userInfo != null) {
            return new SmsCodeAuthenticationToken(userInfo, Collections.emptyList());
        }

        return new SmsCodeAuthenticationToken(buildCustomUserInfo(authenticationToken.getMobile()), Collections.emptyList());
	}

    // build a mobile custom token
    private SecurityUserInfo buildCustomUserInfo(String mobile) {
        SecurityUserInfo userInfo = new SecurityUserInfo();
        userInfo.setLoginId(LoginInfoService.Type.MOBILE.name().toLowerCase() + "_" + mobile);
        userInfo.setUsername(mobile);
        userInfo.setMobile(mobile);
        userInfo.setNotRegistered(true);
        return userInfo;
    }

	@Override
	public boolean supports(Class<?> authentication) {
		return SmsCodeAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
