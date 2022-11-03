package cc.powind.security.core.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DefaultLogoutSuccessHandler implements LogoutSuccessHandler {

    private final Log log = LogFactory.getLog(getClass());

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        if (authentication == null || authentication.getPrincipal() == null) {
            return;
        }

        // 打印个日志
        User principal = (User) authentication.getPrincipal();
        log.info(principal.getUsername() + ", 已经退出！");

        // 跳转到登录验证页
        redirectStrategy.sendRedirect(request, response, "/authentication/require");
    }
}
