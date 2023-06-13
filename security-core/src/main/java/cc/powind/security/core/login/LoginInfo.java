package cc.powind.security.core.login;

public interface LoginInfo {

    SecurityUserInfo getUserInfo();

    /**
     * identify id globally unique
     */
    String getIdentifyId();

    /**
     * username、 mobile、email、wxwork、wx、gitlab、github
     */
    LoginInfoService.Type getType();
}
