package cc.powind.security.token.service;

import cc.powind.security.token.model.Token;
import cc.powind.security.token.model.VerifyToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        return new VerifyToken(getApplyId(request), getValidateCodeId(request), createCode(getDefaultLen(request)), getTimeout(), getIdentityId(request));
    }

    private String getIdentityId(HttpServletRequest request) {
        return request.getParameter(identityIdParameterName);
    }

    @Override
    protected void send(VerifyToken code, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("发送短信：" +  code);
    }

    @Override
    protected Token getTokenFromRequest(HttpServletRequest request) {
        VerifyToken verifyCode = new VerifyToken();
        verifyCode.setCode(getCode(request));
        verifyCode.setIdentifyId(getIdentityId(request));
        return verifyCode;
    }
}
