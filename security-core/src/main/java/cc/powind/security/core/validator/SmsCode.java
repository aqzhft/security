package cc.powind.security.core.validator;

/**
 * 短信验证码
 */
public class SmsCode extends BaseValidateCode {

    /**
     * 手机号
     */
    private String mobile;

    public SmsCode() {
    }

    public SmsCode(String sessionId, String code, Long timeout, String mobile) {
        super(sessionId, code, timeout);
        this.mobile = mobile;

    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public boolean isEqual(ValidateCode validateCode) {

        if (!(validateCode instanceof SmsCode)) {
            return false;
        }

        SmsCode requestCode = (SmsCode) validateCode;

        // 需要校验下手机号是否一致
        if (requestCode.getMobile() == null || !requestCode.getMobile().trim().equals(this.mobile)) {
            return false;
        }

        return super.isEqual(validateCode);
    }
}
