package cc.powind.security.token;

import cc.powind.security.token.exception.TokenInvalidException;
import cc.powind.security.token.model.Token;
import cc.powind.security.token.service.TokenService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class TokenIntercept {

    private List<TokenService<? extends Token>> tokenServices;

    public TokenIntercept(List<TokenService<? extends Token>> tokenServices) {
        this.tokenServices = tokenServices;
    }

    public void intercept(HttpServletRequest request, HttpServletResponse response) throws TokenInvalidException {
        if (tokenServices != null && !tokenServices.isEmpty()) {
            for (TokenService<? extends Token> service: tokenServices) {
                service.validate(request);
            }
        }
    }
}
