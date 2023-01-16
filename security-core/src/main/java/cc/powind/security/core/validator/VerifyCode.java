package cc.powind.security.core.validator;

/**
 * 验证码
 */
public class VerifyCode extends BaseValidateCode {

    /**
     * user's identify code
     */
    private String identifyId;

    public VerifyCode() {
    }

    public VerifyCode(String sessionId, String code, Long timeout, String identifyId) {
        super(sessionId, code, timeout);
        this.identifyId = identifyId;

    }

    public String getIdentifyId() {
        return identifyId;
    }

    public void setIdentifyId(String identifyId) {
        this.identifyId = identifyId;
    }

    @Override
    public boolean isEqual(ValidateCode validateCode) {

        if (!(validateCode instanceof VerifyCode)) {
            return false;
        }

        VerifyCode requestCode = (VerifyCode) validateCode;

        // 需要校验下手机号是否一致
        if (requestCode.getIdentifyId() == null || !requestCode.getIdentifyId().trim().equals(this.identifyId)) {
            return false;
        }

        return super.isEqual(validateCode);
    }
}
