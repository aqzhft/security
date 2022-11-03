package cc.powind.security.core.validator;

/**
 * 校验码
 */
public interface ValidateCode {

    /**
     * 校验码客户端标识
     *
     * @return 标识编号
     */
    String getId();

    /**
     * 校验码编号
     *
     * @return 编号
     */
    String getCode();

    /**
     * 校验码是否过期
     *
     * @return 是否过期
     */
    boolean expired();

    /**
     * 判断校验码是否一致
     *
     * @param code 校验码
     * @return bool
     */
    boolean isEqual(ValidateCode code);
}
