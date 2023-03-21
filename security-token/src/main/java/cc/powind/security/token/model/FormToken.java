package cc.powind.security.token.model;

public class FormToken extends AbstractToken {

    public FormToken() {}

    public FormToken(String applyId, String sessionId, String code, Long timeout) {
        super(applyId, sessionId, code, timeout);
    }
}
