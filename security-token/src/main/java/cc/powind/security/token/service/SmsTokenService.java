package cc.powind.security.token.service;

import cc.powind.security.token.model.SmsToken;
import cc.powind.security.token.model.Token;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SmsTokenService extends AbstractTokenService<SmsToken> {

    private String tokenParameterMobileName = "mobile";

    public String getTokenParameterMobileName() {
        return tokenParameterMobileName;
    }

    public void setTokenParameterMobileName(String tokenParameterMobileName) {
        this.tokenParameterMobileName = tokenParameterMobileName;
    }

    @Override
    protected SmsToken doCreate(HttpServletRequest request) {
        return new SmsToken(getApplyId(request), getValidateCodeId(request), createCode(getDefaultLen(request)), getTimeout(), getMobile(request));
    }

    private String getMobile(HttpServletRequest request) {
        return request.getParameter(tokenParameterMobileName);
    }

    @Override
    protected void send(SmsToken code, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("发送短信：" + code);
    }

    @Override
    protected Token getTokenFromRequest(HttpServletRequest request) {
        SmsToken smsCode = new SmsToken();
        smsCode.setCode(getCode(request));
        smsCode.setMobile(getMobile(request));
        return smsCode;
    }
}
