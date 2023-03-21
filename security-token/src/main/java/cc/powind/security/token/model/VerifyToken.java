package cc.powind.security.token.model;

public class VerifyToken extends AbstractToken {

    private String identifyId;

    public VerifyToken() {
    }

    public VerifyToken(String applyId, String sessionId, String code, Long timeout, String identifyId) {
        super(applyId, sessionId, code, timeout);
        this.identifyId = identifyId;
    }

    public String getIdentifyId() {
        return identifyId;
    }

    public void setIdentifyId(String identifyId) {
        this.identifyId = identifyId;
    }

    @Override
    public boolean isEqual(Token token) {

        if (!(token instanceof VerifyToken)) {
            return false;
        }

        VerifyToken requestCode = (VerifyToken) token;

        // 需要校验下识别号是否一致
        if (requestCode.getIdentifyId() == null || !requestCode.getIdentifyId().trim().equals(this.identifyId)) {
            return false;
        }

        return super.isEqual(token);
    }
}
