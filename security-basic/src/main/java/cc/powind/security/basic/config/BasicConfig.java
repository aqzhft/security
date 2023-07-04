package cc.powind.security.basic.config;

import cc.powind.security.assembly.config.AssemblyConfigureAdapter;
import cc.powind.security.assembly.config.SecurityConfig;
import cc.powind.security.core.login.LoginInfo;
import cc.powind.security.core.login.LoginInfoImpl;
import cc.powind.security.core.login.LoginInfoService;
import cc.powind.security.core.login.SecurityUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@Import(SecurityConfig.class)
public class BasicConfig extends AssemblyConfigureAdapter {

    /**
     * loginId作为系统内访问的唯一标识
     */
    public static final List<SecurityUserInfo> userCache = new ArrayList<>();

    @Autowired(required = false) PasswordEncoder passwordEncoder;

    /**
     * 三种登录方式： username、mobile、email
     *
     */
    private static final List<LoginInfo> loginInfoSet = new ArrayList<>();

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

        loginInfoSet.add(new LoginInfoImpl(userCache.get(1), "167", LoginInfoService.Type.GITLAB));
    }

    private SecurityUserInfo createSecurityUserInfo(String loginId, String name, String password, String mobile, String email) {
        SecurityUserInfo userInfo = new SecurityUserInfo();
        userInfo.setLoginId(loginId);
        userInfo.setUsername(name);
        userInfo.setPassword(password);
        userInfo.setMobile(mobile);
        userInfo.setEmail(email);
        // userInfo.getAuthorities().add(new SimpleGrantedAuthority("ADMIN"));
        return userInfo;
    }

    @Override
    protected Map<String, LoginInfo> loginInfoMappings() {
        return loginInfoSet.stream().collect(Collectors.toMap(LoginInfo::getIdentifyId, bean -> bean, (a, b) -> a));
    }
}
