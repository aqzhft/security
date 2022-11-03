package cc.powind.security.core.validator;

/**
 * 校验码存储器
 */
public interface ValidateCodeRepository {

    /**
     * 保存校验码到存储器
     *
     * @param code 校验码
     */
    void save(ValidateCode code);

    /**
     * 将指定的校验码从存储器里删除
     *
     * @param id 唯一标识
     */
    void remove(String id);

    /**
     * 根据唯一标识查询校验码
     *
     * @param id 唯一标识
     * @return validate code
     */
    ValidateCode get(String id);
}
