package cc.powind.security.core.login;

@FunctionalInterface
public interface LoginInfoService {

    /**
     * load login info by identify id and type
     *
     * @param identifyId third party unique id
     * @param type       login way type
     * @return login info
     */
    SecurityUserInfo load(String identifyId, Type type);

    default SecurityUserInfo load(String identifyId) {
        return load(identifyId, null);
    }

    enum Type {
        PASSWORD, MOBILE, EMAIL, GITLAB, GITHUB, WXWORK, WECHAT
    }
}
