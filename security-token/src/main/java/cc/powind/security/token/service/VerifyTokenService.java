package cc.powind.security.token.service;

import cc.powind.security.token.model.Token;
import cc.powind.security.token.model.VerifyToken;

import javax.servlet.http.HttpServletRequest;

public class VerifyTokenService extends AbstractTokenService<VerifyToken> {

    private String identityIdParameterName = "identifyId";

    public String getIdentityIdParameterName() {
        return identityIdParameterName;
    }

    public void setIdentityIdParameterName(String identityIdParameterName) {
        this.identityIdParameterName = identityIdParameterName;
    }

    @Override
    protected VerifyToken doCreate(HttpServletRequest request) {
        return new VerifyToken(getApplyId(request), getValidateCodeId(request), createCode(getLen()), getTimeout(), getIdentityId(request));
    }

    private String getIdentityId(HttpServletRequest request) {
        return request.getParameter(identityIdParameterName);
    }

    @Override
    protected Token getTokenFromRequest(HttpServletRequest request) {
        VerifyToken verifyCode = new VerifyToken();
        verifyCode.setCode(getCode(request));
        verifyCode.setIdentifyId(getIdentityId(request));
        return verifyCode;
    }
}
