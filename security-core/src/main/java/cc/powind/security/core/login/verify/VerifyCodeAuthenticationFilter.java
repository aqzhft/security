package cc.powind.security.core.login.verify;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * {@link UsernamePasswordAuthenticationFilter}
 */
public class VerifyCodeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String DEFAULT_IDENTIFY_ID_PARAMETER_NAME = "identifyId";

	private String identifyIdParameterName = DEFAULT_IDENTIFY_ID_PARAMETER_NAME;
	private boolean postOnly = true;

	public VerifyCodeAuthenticationFilter(String pattern) {
		super(new AntPathRequestMatcher(pattern, "POST"));
	}

	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        if (postOnly && !request.getMethod().equalsIgnoreCase("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}

		String identifyId = obtainIdentifyId(request);

		if (identifyId == null) {
            identifyId = "";
		}

        identifyId = identifyId.trim();

		VerifyCodeAuthenticationToken authRequest = new VerifyCodeAuthenticationToken(identifyId);

		setDetails(request, authRequest);

		return this.getAuthenticationManager().authenticate(authRequest);
	}

	protected String obtainIdentifyId(HttpServletRequest request) {
		return request.getParameter(identifyIdParameterName);
	}

	protected void setDetails(HttpServletRequest request, VerifyCodeAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
	}

    public String getIdentifyIdParameterName() {
        return identifyIdParameterName;
    }

    public void setIdentifyIdParameterName(String identifyIdParameterName) {
        Assert.isTrue(StringUtils.isNoneBlank(identifyIdParameterName), "verify code parameter name not null");
        this.identifyIdParameterName = identifyIdParameterName;
    }

    public boolean isPostOnly() {
        return postOnly;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }
}
