package cc.powind.security.core.login;

public interface LoginInfo {

    /**
     * current system unique login id
     */
    String getLoginId();

    /**
     * identify id
     */
    String getIdentifyId();

    /**
     * username、 mobile、email、wxwork、wx、gitlab、github
     */
    String getType();
}
