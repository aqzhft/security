package cc.powind.security.token.service;

import cc.powind.security.token.model.Token;
import cc.powind.security.token.model.VerifyToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class VerifyTokenService extends AbstractTokenService<VerifyToken> {

    private String identityIdParameterName = "identifyId";

    private TokenTransmit tokenTransmit;

    public String getIdentityIdParameterName() {
        return identityIdParameterName;
    }

    public void setIdentityIdParameterName(String identityIdParameterName) {
        this.identityIdParameterName = identityIdParameterName;
    }

    public TokenTransmit getTokenTransmit() {
        return tokenTransmit;
    }

    public void setTokenTransmit(TokenTransmit tokenTransmit) {
        this.tokenTransmit = tokenTransmit;
    }

    @Override
    protected VerifyToken doCreate(HttpServletRequest request) {
        return new VerifyToken(getApplyId(request), getValidateCodeId(request), createCode(getDefaultLen(request)), getTimeout(), getIdentityId(request));
    }

    private String getIdentityId(HttpServletRequest request) {
        return request.getParameter(identityIdParameterName);
    }

    @Override
    protected void send(VerifyToken code, HttpServletRequest request, HttpServletResponse response) {
        if (tokenTransmit != null) {
            try {
                tokenTransmit.send(code, response);
            } catch (IOException e) {
                log.error("send verify code failed ", e);
            }
        }
    }

    @Override
    protected Token getTokenFromRequest(HttpServletRequest request) {
        VerifyToken verifyCode = new VerifyToken();
        verifyCode.setCode(getCode(request));
        verifyCode.setIdentifyId(getIdentityId(request));
        return verifyCode;
    }
}
