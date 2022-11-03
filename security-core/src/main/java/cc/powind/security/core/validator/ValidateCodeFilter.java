package cc.powind.security.core.validator;

import cc.powind.security.core.exception.ValidateCodeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.Ordered;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ValidateCodeFilter extends OncePerRequestFilter implements Ordered {

    private final Log log = LogFactory.getLog(getClass());

    private List<ValidateCodeService<? extends ValidateCode>> validateCodeServices;

    private AuthenticationFailureHandler authenticationFailureHandler;

    public List<ValidateCodeService<? extends ValidateCode>> getValidateCodeServices() {
        return validateCodeServices;
    }

    public void setValidateCodeServices(List<ValidateCodeService<? extends ValidateCode>> validateCodeServices) {
        this.validateCodeServices = validateCodeServices;
    }

    public AuthenticationFailureHandler getAuthenticationFailureHandler() {
        return authenticationFailureHandler;
    }

    public void setAuthenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {

            if (validateCodeServices != null && !validateCodeServices.isEmpty()) {

                for (ValidateCodeService<? extends ValidateCode> service: validateCodeServices) {
                    service.validate(new ServletWebRequest(request, response));
                }
            }
        } catch (ValidateCodeException e) {

            log.error(e);

            authenticationFailureHandler.onAuthenticationFailure(request, response, e);

            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
