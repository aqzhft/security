package cc.powind.security.core.login.sms;

import cc.powind.security.core.login.LoginInfoService;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;

/**
 * 照着抄 {@link UsernamePasswordAuthenticationToken}
 */
public class SmsCodeAuthenticationToken extends AbstractAuthenticationToken {

    private String mobile;

    private LoginInfoService.Type loginType;

	private Object principal;

	public SmsCodeAuthenticationToken(String mobile, String loginType) {
		super(null);
		this.mobile = mobile;
        this.loginType = getLoginType(loginType);
        Assert.notNull(this.loginType, "sms code login must input login way");
		setAuthenticated(false);
	}

	public SmsCodeAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		super.setAuthenticated(true);
	}

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public LoginInfoService.Type getLoginType() {
        return loginType;
    }

    public void setLoginType(LoginInfoService.Type loginType) {
        this.loginType = loginType;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public void setPrincipal(Object principal) {
        this.principal = principal;
    }

    @Override
	public Object getCredentials() {
		return null;
	}

	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		if (isAuthenticated) {
			throw new IllegalArgumentException(
					"Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
		}

		super.setAuthenticated(false);
	}

	@Override
	public void eraseCredentials() {
		super.eraseCredentials();
	}

    private LoginInfoService.Type getLoginType(String loginType) {
        for (LoginInfoService.Type type : LoginInfoService.Type.values()) {
            if (type.name().equalsIgnoreCase(loginType)) {
                return type;
            }
        }
        return null;
    }
}