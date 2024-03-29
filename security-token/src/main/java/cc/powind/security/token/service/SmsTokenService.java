package cc.powind.security.token.service;

import cc.powind.security.token.model.SmsToken;
import cc.powind.security.token.model.Token;

import javax.servlet.http.HttpServletRequest;

public class SmsTokenService extends AbstractTokenService<SmsToken> {

    private final static String DEFAULT_TOKEN_PARAMETER_MOBILE = "sms";

    @Override
    protected SmsToken doCreate(HttpServletRequest request) {
        return new SmsToken(getApplyId(request), getValidateCodeId(request), createCode(getLen()), getTimeout(), getMobile(request));
    }

    private String getMobile(HttpServletRequest request) {
        return request.getParameter(DEFAULT_TOKEN_PARAMETER_MOBILE);
    }

    @Override
    protected Token getTokenFromRequest(HttpServletRequest request) {
        SmsToken smsCode = new SmsToken();
        smsCode.setCode(getCode(request));
        smsCode.setMobile(getMobile(request));
        return smsCode;
    }
}
