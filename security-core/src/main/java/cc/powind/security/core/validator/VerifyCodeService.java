package cc.powind.security.core.validator;

import cc.powind.security.core.exception.ValidateCodeException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.IOException;

public class VerifyCodeService extends AbstractValidateCodeService<VerifyCode> {

    private static final String validateCodeParameterName = "identifyId";

    private ValidateCodeSender validateCodeSender;

    public ValidateCodeSender getValidateCodeSender() {
        return validateCodeSender;
    }

    public void setValidateCodeSender(ValidateCodeSender validateCodeSender) {
        this.validateCodeSender = validateCodeSender;
    }

    @Override
    protected VerifyCode doCreate(ServletWebRequest webRequest) {
        try {

            String identifyId = ServletRequestUtils.getStringParameter(webRequest.getRequest(), validateCodeParameterName);
            int len = ServletRequestUtils.getIntParameter(webRequest.getRequest(), getValidateCodeParameterLen(), getLen());

            return new VerifyCode(getValidateCodeId(webRequest), createCode(len), getTimeout(), identifyId);
        } catch (ServletRequestBindingException e) {
            throw new ValidateCodeException("用户的登录标识不能为空！");
        }
    }

    @Override
    protected void send(VerifyCode code, ServletWebRequest request) {
        if (validateCodeSender != null) {
            try {
                validateCodeSender.send(code, request.getResponse());
            } catch (IOException e) {
                log.error("send verify code failed ", e);
            }
        }
    }

    @Override
    protected ValidateCode getValidateCodeFromRequest(ServletWebRequest webRequest) {
        VerifyCode verifyCode = new VerifyCode();
        String code = webRequest.getRequest().getParameter(getValidateCodeParameterName());
        String identifyId = webRequest.getRequest().getParameter(validateCodeParameterName);
        verifyCode.setCode(code);
        verifyCode.setIdentifyId(identifyId);
        return verifyCode;
    }
}
