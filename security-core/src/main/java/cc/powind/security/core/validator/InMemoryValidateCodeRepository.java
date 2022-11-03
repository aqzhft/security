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
        cacheMap.put(code.getId(), code);
    }

    @Override
    public void remove(String id) {
        cacheMap.remove(id);
    }

    @Override
    public ValidateCode get(String id) {

        ValidateCode validateCode = cacheMap.get(id);

        if (validateCode == null) {
            return null;
        }

        if (validateCode.expired()) {
            remove(validateCode.getId());
        }

        return validateCode;
    }
}
