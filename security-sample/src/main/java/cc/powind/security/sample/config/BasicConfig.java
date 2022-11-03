package cc.powind.security.sample.config;

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

@ComponentScan("cc.powind.security")
@Configuration("name")
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
}
