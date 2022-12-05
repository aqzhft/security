package cc.powind.security.core.validator;

public class FormCode extends BaseValidateCode {

    public FormCode() {}

    public FormCode(String sessionId, String code, Long timeout) {
        super(sessionId, code, timeout);
    }
}
