package cc.powind.security.core.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * 照着抄 {@link UsernamePasswordAuthenticationToken}
 */
public class SmsCodeAuthenticationToken extends AbstractAuthenticationToken {

    /**
     * 手机号
     */
	private final Object principal;

	public SmsCodeAuthenticationToken(Object principal) {
		super(null);
		this.principal = principal;
		setAuthenticated(false);
	}

	public SmsCodeAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		super.setAuthenticated(true);
	}

	public Object getPrincipal() {
		return this.principal;
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
	
}