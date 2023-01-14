package cc.powind.security.sample.config;

import cc.powind.security.core.authorize.RbacService;
import cc.powind.security.core.login.LoginIdService;
import cc.powind.security.core.login.LoginInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Configuration
@ComponentScan("cc.powind.security")
public class BasicConfig {

    /**
     * loginId作为系统内访问的唯一标识
     */
    public static final Set<UserInfo> userCache = new HashSet<>();

    @Autowired(required = false) PasswordEncoder passwordEncoder;

    /**
     * 三种登录方式： username、mobile、email
     *
     */
    private static final Set<LoginInfoImpl> loginInfoSet = new HashSet<>();

    @PostConstruct
    public void init() {

        userCache.add(new UserInfo("1", "admin", passwordEncoder.encode("admin"), "15212345678", "admin@admin.com"));
        userCache.add(new UserInfo("2", "lisa", passwordEncoder.encode("lisa"), "15212341234", "lisa@lisa.com"));
        userCache.add(new UserInfo("3", "tom", passwordEncoder.encode("tom"), "15211112222", "tom@tom.com"));


        for (UserInfo info : userCache) {

            loginInfoSet.add(new LoginInfoImpl(info.getLoginId(), info.getName(), "username", true));

            loginInfoSet.add(new LoginInfoImpl(info.getLoginId(), info.getMobile(), "mobile", true));

            loginInfoSet.add(new LoginInfoImpl(info.getLoginId(), info.getEmail(), "email", true));
        }

        loginInfoSet.add(new LoginInfoImpl("3", "167", "gitlab", false));
    }

    @Bean
    public UserDetailsService userDetailsService() {

        return new UserDetailsService() {

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

                UserInfo userInfo = null;

                for (LoginInfoImpl loginInfo : loginInfoSet) {
                    if (loginInfo.getIdentifyId().equals(username)) {
                        userInfo = getUserInfo(loginInfo.getLoginId());
                    }
                }

                if (userInfo == null) {
                    throw new UsernameNotFoundException("未查询到【" + username + "】相关的用户");
                }

                return new User(userInfo.getLoginId(), userInfo.getPassword(), Collections.emptyList());
            }
        };
    }

    @Bean
    public LoginIdService loginIdService() {
        return (identifyId, type) -> {

            for (LoginInfoImpl identifyCode : loginInfoSet) {

                if (!identifyCode.enable) {
                    continue;
                }

                if (type.equals(identifyCode.getType()) && identifyId.equals(identifyCode.getIdentifyId())) {
                    return identifyCode;
                }
            }

            return null;
        };
    }

    private UserInfo getUserInfo(String loginId) {
        for (UserInfo info : userCache) {
            if (loginId.equals(info.getLoginId())) {
                return info;
            }
        }
        return null;
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

    class UserInfo {

        private String loginId;

        private String name;

        private String password;

        private String mobile;

        private String email;

        public UserInfo(String loginId, String name, String password, String mobile, String email) {
            this.loginId = loginId;
            this.name = name;
            this.password = password;
            this.mobile = mobile;
            this.email = email;
        }

        public String getLoginId() {
            return loginId;
        }

        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    class LoginInfoImpl implements LoginInfo {

        private String loginId;

        private String identifyId;

        private String type;

        /**
         * 是否开启
         */
        private boolean enable;

        public LoginInfoImpl(String loginId, String identifyId, String type, boolean enable) {
            this.loginId = loginId;
            this.identifyId = identifyId;
            this.type = type;
            this.enable = enable;
        }

        public String getLoginId() {
            return loginId;
        }

        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }

        public String getIdentifyId() {
            return identifyId;
        }

        public void setIdentifyId(String identifyId) {
            this.identifyId = identifyId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }
    }
}
