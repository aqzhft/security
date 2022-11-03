package cc.powind.security.core.validator;

import cc.powind.security.core.exception.ValidateCodeException;
import org.springframework.web.context.request.ServletWebRequest;

public interface ValidateCodeService <T extends ValidateCode> {

    /**
     * 创建验证码
     *
     * @param webRequest request
     */
    T create(ServletWebRequest webRequest);

    /**
     * 校验
     *
     * @param webRequest request
     */
    void validate(ServletWebRequest webRequest) throws ValidateCodeException;
}
