package cc.powind.security.assemble.config;

import cc.powind.security.core.authorize.RbacService;
import cc.powind.security.core.login.LoginInfoService;
import cc.powind.security.token.service.TokenNotifier;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.security.KeyPair;

public interface AssembleConfigure {

    /**
     * 用户名密码验证需要提取用户信息
     */
    UserDetailsService userDetailsService();

    /**
     * 登录标识服务
     */
    LoginInfoService loginInfoService();

    /**
     * 权限服务配置
     */
    RbacService rbacService();

    /**
     * 验证码通知
     */
    TokenNotifier tokenNotifier();

    /**
     * oidc authorization key
     */
    KeyPair authorizeKey();

    /**
     * authorization server register client info
     */
    RegisteredClientRepository authorizationClientRepository();
}
