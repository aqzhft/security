package cc.powind.security.token.service;

import cc.powind.security.token.model.EmailToken;
import cc.powind.security.token.model.Token;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EmailTokenService extends AbstractTokenService<EmailToken> {

    private final static String DEFAULT_TOKEN_PARAMETER_EMAIL = "sms";

    @Override
    protected EmailToken doCreate(HttpServletRequest request) {
        return new EmailToken(getApplyId(request), getValidateCodeId(request), createCode(getLen()), getTimeout(), getEmail(request));
    }

    private String getEmail(HttpServletRequest request) {
        return request.getParameter(DEFAULT_TOKEN_PARAMETER_EMAIL);
    }

    @Override
    protected void send(EmailToken code, HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("发送token: " + code);
    }

    @Override
    protected Token getTokenFromRequest(HttpServletRequest request) {
        EmailToken smsCode = new EmailToken();
        smsCode.setCode(getCode(request));
        smsCode.setEmail(getEmail(request));
        return smsCode;
    }
}
