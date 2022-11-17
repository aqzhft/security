package cc.powind.security.core.login.wxwork;

import cc.powind.security.core.properties.WxworkProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.web.client.RestTemplate;

public class WxworkAuthenticationConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private WxworkProperties properties;

    public WxworkProperties getProperties() {
        return properties;
    }

    public void setProperties(WxworkProperties properties) {
        this.properties = properties;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

        WxworkAuthenticationFilter wxworkAuthenticationFilter = new WxworkAuthenticationFilter();
        wxworkAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));

        WxworkAuthenticationProvider wxworkAuthenticationProvider = new WxworkAuthenticationProvider();
        wxworkAuthenticationProvider.setRestOperations(new RestTemplate());
        wxworkAuthenticationProvider.setProperties(properties);

        http.authenticationProvider(wxworkAuthenticationProvider).addFilterAfter(wxworkAuthenticationFilter, OAuth2LoginAuthenticationFilter.class);
    }
}
