package cc.powind.security.token.model;

import java.time.Instant;

public abstract class AbstractToken implements Token {

    private String applyId;

    private String sessionId;

    private String code;

    private Instant createTime;

    private Long timeout;

    public AbstractToken() {
    }

    public AbstractToken(String applyId, String sessionId, String code, Long timeout) {
        this.applyId = applyId;
        this.sessionId = sessionId;
        this.code = code;
        this.createTime = Instant.now();
        this.timeout = timeout;
    }

    @Override
    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public Instant getExpireTime() {
        return this.createTime.plusSeconds(this.timeout);
    }

    @Override
    public boolean expired() {
        return !createTime.plusSeconds(timeout).isAfter(Instant.now());
    }

    @Override
    public boolean isEqual(Token token) {
        return token != null && token.getCode() != null && token.getCode().equals(this.code);
    }

    @Override
    public String toString() {
        return "BaseValidateCode{" + "sessionId='" + sessionId + '\'' + ", code='" + code + '\'' + ", createTime=" + createTime + ", timeout=" + timeout + '}';
    }
}
