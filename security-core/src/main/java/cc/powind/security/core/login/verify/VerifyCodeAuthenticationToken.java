package cc.powind.security.core.login.verify;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * 照着抄 {@link UsernamePasswordAuthenticationToken}
 */
public class VerifyCodeAuthenticationToken extends AbstractAuthenticationToken {

    /**
     * user's identify code
     */
	private final Object principal;

	public VerifyCodeAuthenticationToken(Object principal) {
		super(null);
		this.principal = principal;
		setAuthenticated(false);
	}

	public VerifyCodeAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
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