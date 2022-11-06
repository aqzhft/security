package cc.powind.security.core.login;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DefaultAuthenticationFailureHandler implements AuthenticationFailureHandler {

	private final Log logger = LogFactory.getLog(this.getClass());
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {
		
		logger.error(exception);

		response.setContentType("application/json;charset=utf-8");
		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.getWriter().write(exception.getMessage());
	}
}
