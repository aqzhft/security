package cc.powind.security.core.login;

public class LoginInfoImpl implements LoginInfo {

    private SecurityUserInfo userInfo;

    private String identifyId;

    private LoginInfoService.Type type;

    public LoginInfoImpl(SecurityUserInfo userInfo, String identifyId, LoginInfoService.Type type) {
        this.userInfo = userInfo;
        this.identifyId = identifyId;
        this.type = type;
    }

    @Override
    public SecurityUserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(SecurityUserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String getIdentifyId() {
        return identifyId;
    }

    public void setIdentifyId(String identifyId) {
        this.identifyId = identifyId;
    }

    @Override
    public LoginInfoService.Type getType() {
        return type;
    }

    public void setType(LoginInfoService.Type type) {
        this.type = type;
    }
}
