package cc.powind.security.token.model;

/**
 * 校验码
 */
public interface Token {

    /**
     * user apply for a token need a unique id
     *
     * @return applyId
     */
    String getApplyId();

    /**
     * @return sessionId
     */
    String getSessionId();

    /**
     * validate code
     *
     * @return 编号
     */
    String getCode();

    /**
     * whether the token has expired
     *
     * @return 是否过期
     */
    boolean expired();

    /**
     * check the token for consistency
     *
     * @param code token
     * @return bool
     */
    boolean isEqual(Token code);
}
