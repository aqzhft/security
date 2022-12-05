package cc.powind.security.core.validator;

import cc.powind.security.core.exception.ValidateCodeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

public class SmsCodeService extends AbstractValidateCodeService<SmsCode> {

    private final Log log = LogFactory.getLog(getClass());

    private String validateCodeParameterMobileName = "mobile";

    public String getValidateCodeParameterMobileName() {
        return validateCodeParameterMobileName;
    }

    public void setValidateCodeParameterMobileName(String validateCodeParameterMobileName) {
        this.validateCodeParameterMobileName = validateCodeParameterMobileName;
    }

    @Override
    protected SmsCode doCreate(ServletWebRequest webRequest) {
        try {
            String mobile = ServletRequestUtils.getStringParameter(webRequest.getRequest(), "mobile");
            int len = ServletRequestUtils.getIntParameter(webRequest.getRequest(), getValidateCodeParameterLen(), getLen());

            return new SmsCode(getValidateCodeId(webRequest), createCode(len), getTimeout(), mobile);
        } catch (ServletRequestBindingException e) {
            throw new ValidateCodeException("创建校验码手机号不能为空！");
        }
    }

    @Override
    protected void send(SmsCode code, ServletWebRequest request) {
        // 默认直接打印出来
        log.info("send sms code: mobile -> " + code.getMobile() + ", validate code -> " + code.getCode());
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
