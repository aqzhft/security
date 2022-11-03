package cc.powind.security.core.validator;

import java.time.Instant;

public abstract class BaseValidateCode implements ValidateCode {

    /**
     * 唯一标识
     */
    private String id;

    /**
     * 验证码
     */
    private String code;

    /**
     * 创建时间
     */
    private Instant createTime;

    /**
     * 有效时长
     */
    private Long expireLength;

    public BaseValidateCode() {
    }

    public BaseValidateCode(String id, String code, Long expireLength) {
        this.id = id;
        this.code = code;
        this.createTime = Instant.now();
        this.expireLength = expireLength;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Long getExpireLength() {
        return expireLength;
    }

    public void setExpireLength(Long expireLength) {
        this.expireLength = expireLength;
    }

    public Instant getExpireTime() {
        return this.createTime.plusSeconds(this.expireLength);
    }

    @Override
    public boolean expired() {
        return !createTime.plusSeconds(expireLength).isAfter(Instant.now());
    }

    @Override
    public boolean isEqual(ValidateCode validateCode) {
        return validateCode != null && validateCode.getCode() != null && validateCode.getCode().equals(this.code);
    }
}
