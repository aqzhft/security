package cc.powind.security.core.validator;

import cc.powind.security.core.exception.ValidateCodeException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.IOException;

public class SmsCodeService extends AbstractValidateCodeService<SmsCode> {

    private String validateCodeParameterMobileName = "mobile";

    private ValidateCodeSender validateCodeSender;

    public String getValidateCodeParameterMobileName() {
        return validateCodeParameterMobileName;
    }

    public void setValidateCodeParameterMobileName(String validateCodeParameterMobileName) {
        this.validateCodeParameterMobileName = validateCodeParameterMobileName;
    }

    public ValidateCodeSender getValidateCodeSender() {
        return validateCodeSender;
    }

    public void setValidateCodeSender(ValidateCodeSender validateCodeSender) {
        this.validateCodeSender = validateCodeSender;
    }

    @Override
    protected SmsCode doCreate(ServletWebRequest webRequest) {
        try {
            String mobile = ServletRequestUtils.getStringParameter(webRequest.getRequest(), validateCodeParameterMobileName);
            int len = ServletRequestUtils.getIntParameter(webRequest.getRequest(), getValidateCodeParameterLen(), getLen());

            return new SmsCode(getValidateCodeId(webRequest), createCode(len), getTimeout(), mobile);
        } catch (ServletRequestBindingException e) {
            throw new ValidateCodeException("创建校验码手机号不能为空！");
        }
    }

    @Override
    protected void send(SmsCode code, ServletWebRequest request) {

        if (validateCodeSender != null) {
            try {
                validateCodeSender.send(code, new String[] {"mobile"}, request.getResponse());
            } catch (IOException e) {
                log.error("send sms code to client failed", e);
            }
        }

    }

    @Override
    protected ValidateCode getValidateCodeFromRequest(ServletWebRequest webRequest) {
        SmsCode smsCode = new SmsCode();
        String code = webRequest.getRequest().getParameter(getValidateCodeParameterName());
        String mobile = webRequest.getRequest().getParameter(validateCodeParameterMobileName);
        smsCode.setCode(code);
        smsCode.setMobile(mobile);
        return smsCode;
    }
}
