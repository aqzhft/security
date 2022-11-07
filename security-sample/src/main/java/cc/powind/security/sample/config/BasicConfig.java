package cc.powind.security.sample.config;

import cc.powind.security.core.authorize.RbacService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan("cc.powind.security")
public class BasicConfig {

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        return new UserDetailsService() {

            private final Map<String, String> map = new HashMap<>();

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                map.putIfAbsent(username, passwordEncoder.encode(username));
                return new User(username, map.get(username), Collections.emptyList());
            }
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
