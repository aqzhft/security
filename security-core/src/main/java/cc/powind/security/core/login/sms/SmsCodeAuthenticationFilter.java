package cc.powind.security.core.login.sms;

import cc.powind.security.core.login.LoginInfoService;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * {@link UsernamePasswordAuthenticationFilter}
 */
public class SmsCodeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	public static final String SPRING_SECURITY_SMS_KEY = "sms";
	public static final String SPRING_SECURITY_LOGIN_WAY_KEY = "loginWay";
	private String smsParameter = SPRING_SECURITY_SMS_KEY;
	private String loginWayParameterName = SPRING_SECURITY_LOGIN_WAY_KEY;
    private final String[] patterns;
    private final PathMatcher pathMatcher = new AntPathMatcher();

	public SmsCodeAuthenticationFilter(String[] patterns) {
        super(new AntPathRequestMatcher("/don't need this matcher"));
		Assert.notNull(patterns, "authentication patterns cannot be null");
        this.patterns = patterns;
	}

	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        if (!request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}

		String sms = obtainSms(request);
        String loginWay = obtainLoginWay(request);

		if (sms == null) {
            sms = "";
		}

        if (loginWay == null) {
            loginWay = LoginInfoService.Type.MOBILE.name();
        }

        sms = sms.trim();
        loginWay = loginWay.trim();

		SmsCodeAuthenticationToken authRequest = new SmsCodeAuthenticationToken(sms, loginWay);

		setDetails(request, authRequest);

		return this.getAuthenticationManager().authenticate(authRequest);
	}

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return Arrays.stream(patterns).anyMatch(pattern -> pathMatcher.match(pattern, request.getRequestURI()));
    }

    protected String obtainSms(HttpServletRequest request) {
		return request.getParameter(smsParameter);
	}

    protected String obtainLoginWay(HttpServletRequest request) {
        return request.getParameter(loginWayParameterName);
    }

	protected void setDetails(HttpServletRequest request, SmsCodeAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
	}

	public void setMobileParameter(String smsParameter) {
		Assert.hasText(smsParameter, "smsCode parameter must not be empty or null");
		this.smsParameter = smsParameter;
	}

	public final String getSmsParameter() {
		return smsParameter;
	}

    public String getLoginWayParameterName() {
        return loginWayParameterName;
    }

    public void setLoginWayParameterName(String loginWayParameterName) {
        this.loginWayParameterName = loginWayParameterName;
    }
}
