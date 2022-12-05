package cc.powind.security.core.validator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存实现
 */
public class InMemoryValidateCodeRepository implements ValidateCodeRepository {

    private final static Map<String, ValidateCode> cacheMap = new ConcurrentHashMap<>();

    @Override
    public void save(ValidateCode code) {
        cacheMap.put(code.getSessionId(), code);
    }

    @Override
    public void remove(String sessionId) {
        cacheMap.remove(sessionId);
    }

    @Override
    public ValidateCode get(String sessionId) {

        ValidateCode validateCode = cacheMap.get(sessionId);

        if (validateCode == null) {
            return null;
        }

        if (validateCode.expired()) {
            remove(validateCode.getSessionId());
        }

        return validateCode;
    }
}
