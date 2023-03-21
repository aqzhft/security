package cc.powind.security.token.model;

public class SmsToken extends AbstractToken {

    private String mobile;

    public SmsToken() {
    }

    public SmsToken(String applyId, String sessionId, String code, Long timeout, String mobile) {
        super(applyId, sessionId, code, timeout);
        this.mobile = mobile;

    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public boolean isEqual(Token token) {

        if (!(token instanceof SmsToken)) {
            return false;
        }

        SmsToken requestCode = (SmsToken) token;

        // 需要校验下手机号是否一致
        if (requestCode.getMobile() == null || !requestCode.getMobile().trim().equals(this.mobile)) {
            return false;
        }

        return super.isEqual(token);
    }
}
