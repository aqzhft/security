package cc.powind.security.token.model;

public class EmailToken extends AbstractToken {

    private String email;

    public EmailToken() {
    }

    public EmailToken(String applyId, String sessionId, String code, Long timeout, String email) {
        super(applyId, sessionId, code, timeout);
        this.email = email;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean isEqual(Token token) {

        if (!(token instanceof EmailToken)) {
            return false;
        }

        EmailToken requestCode = (EmailToken) token;

        // validate email address if the same
        if (requestCode.getEmail() == null || !requestCode.getEmail().trim().equals(this.email)) {
            return false;
        }

        return super.isEqual(token);
    }
}
