package cc.powind.security.basic.config;

import cc.powind.security.core.authorize.RbacService;
import cc.powind.security.core.login.LoginInfoImpl;
import cc.powind.security.core.login.LoginInfoService;
import cc.powind.security.core.login.SecurityUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ComponentScan("cc.powind.security")
public class BasicConfig {

    /**
     * loginId作为系统内访问的唯一标识
     */
    public static final List<SecurityUserInfo> userCache = new ArrayList<>();

    @Autowired(required = false) PasswordEncoder passwordEncoder;

    /**
     * 三种登录方式： username、mobile、email
     *
     */
    private static final List<LoginInfoImpl> loginInfoSet = new ArrayList<>();

    @PostConstruct
    public void init() {

        userCache.add(createSecurityUserInfo("1", "admin", passwordEncoder.encode("admin"), "15212345678", "admin@admin.com"));
        userCache.add(createSecurityUserInfo("2", "lisa", passwordEncoder.encode("lisa"), "15212341234", "lisa@lisa.com"));
        userCache.add(createSecurityUserInfo("3", "tom", passwordEncoder.encode("tom"), "15211112222", "tom@tom.com"));


        for (SecurityUserInfo info : userCache) {

            loginInfoSet.add(new LoginInfoImpl(info, info.getName(), LoginInfoService.Type.PASSWORD));

            loginInfoSet.add(new LoginInfoImpl(info, info.getMobile(), LoginInfoService.Type.MOBILE));

            loginInfoSet.add(new LoginInfoImpl(info, info.getEmail(), LoginInfoService.Type.EMAIL));
        }

        // loginInfoSet.add(new LoginInfoImpl(userCache.get(1), "167", LoginInfoService.Type.GITLAB));
    }

    private SecurityUserInfo createSecurityUserInfo(String loginId, String name, String password, String mobile, String email) {
        SecurityUserInfo userInfo = new SecurityUserInfo();
        userInfo.setLoginId(loginId);
        userInfo.setUsername(name);
        userInfo.setPassword(password);
        userInfo.setMobile(mobile);
        userInfo.setEmail(email);
        return userInfo;
    }

    @Bean
    public UserDetailsService userDetailsService() {

        return new UserDetailsService() {

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

                for (LoginInfoImpl loginInfo : loginInfoSet) {
                    if (loginInfo.getIdentifyId().equals(username)) {
                        return loginInfo.getUserInfo();
                    }
                }

                throw new UsernameNotFoundException("未查询到【" + username + "】相关的用户");
            }
        };
    }

    @Bean
    public LoginInfoService loginIdService() {
        return (identifyId, type) -> {

            for (LoginInfoImpl loginInfo : loginInfoSet) {

                if (type != null && !type.equals(loginInfo.getType())) {
                    continue;
                }

                if (identifyId.equals(loginInfo.getIdentifyId())) {
                    return loginInfo.getUserInfo();
                }
            }

            return null;
        };
    }

    @Bean
    public RbacService rbacService() {

        return (request, authentication) -> {

            // 请求路径
            String uri = request.getRequestURI();

            // 请求方法
            String method = request.getMethod();

            // 需要一个获取权限的接口

//            User user = userService.getCurrentUser();
//
//            if (user != null && StringUtils.isNoneBlank(method)) {
//                for (Privilege privilege : user.getPrivilegeList()) {
//
//                    if (antPathMather.match(privilege.getUrl(), uri) && method.equalsIgnoreCase(privilege.getMethod())) {
//                        return true;
//                    }
//
//                }
//            }

            return true;
        };
    }
}
