package cc.powind.security.core.validator;

import java.time.Instant;

public abstract class BaseValidateCode implements ValidateCode {

    /**
     * 唯一标识
     */
    private String sessionId;

    /**
     * 验证码
     */
    private String code;

    /**
     * 创建时间
     */
    private Instant createTime;

    /**
     * 有效时长（秒）
     */
    private Long timeout;

    public BaseValidateCode() {
    }

    public BaseValidateCode(String sessionId, String code, Long timeout) {
        this.sessionId = sessionId;
        this.code = code;
        this.createTime = Instant.now();
        this.timeout = timeout;
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
    public boolean isEqual(ValidateCode validateCode) {
        return validateCode != null && validateCode.getCode() != null && validateCode.getCode().equals(this.code);
    }
}
