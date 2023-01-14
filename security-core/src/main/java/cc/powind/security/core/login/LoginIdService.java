package cc.powind.security.core.login;

@FunctionalInterface
public interface LoginIdService {

    /**
     * load login info by identify id and type
     *
     * @param identifyId third party unique id
     * @param type       login way type
     * @return login info
     */
    LoginInfo load(String identifyId, String type);
}
