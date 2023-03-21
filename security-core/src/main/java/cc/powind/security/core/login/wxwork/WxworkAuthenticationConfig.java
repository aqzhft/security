package cc.powind.security.core.login.wxwork;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.DefaultSecurityFilterChain;

public class WxworkAuthenticationConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private WxworkAuthenticationFilter wxworkAuthenticationFilter;

    private WxworkAuthenticationProvider wxworkAuthenticationProvider;

    private WxworkOAuth2RedirectFilter wxworkOAuth2RedirectFilter;

    public WxworkAuthenticationFilter getWxworkAuthenticationFilter() {
        return wxworkAuthenticationFilter;
    }

    public void setWxworkAuthenticationFilter(WxworkAuthenticationFilter wxworkAuthenticationFilter) {
        this.wxworkAuthenticationFilter = wxworkAuthenticationFilter;
    }

    public WxworkAuthenticationProvider getWxworkAuthenticationProvider() {
        return wxworkAuthenticationProvider;
    }

    public void setWxworkAuthenticationProvider(WxworkAuthenticationProvider wxworkAuthenticationProvider) {
        this.wxworkAuthenticationProvider = wxworkAuthenticationProvider;
    }

    public WxworkOAuth2RedirectFilter getWxworkOAuth2RedirectFilter() {
        return wxworkOAuth2RedirectFilter;
    }

    public void setWxworkOAuth2RedirectFilter(WxworkOAuth2RedirectFilter wxworkOAuth2RedirectFilter) {
        this.wxworkOAuth2RedirectFilter = wxworkOAuth2RedirectFilter;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        wxworkAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        http.addFilterBefore(wxworkOAuth2RedirectFilter, OAuth2LoginAuthenticationFilter.class);
        http.authenticationProvider(wxworkAuthenticationProvider).addFilterAfter(wxworkAuthenticationFilter, OAuth2LoginAuthenticationFilter.class);
    }
}
