package cc.powind.security.core.login;

import cc.powind.security.core.authorize.RbacService;
import cc.powind.security.core.properties.SecurityProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class SecurityEntrypoint {

    @Autowired
    private SecurityProperties properties;

    @Autowired
    private RbacService rbacService;

    @GetMapping("/login")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String login(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String loginWay = request.getParameter("way");
        request.setAttribute("loginWay", StringUtils.isBlank(loginWay) ? properties.getPage().getLoginPage().getLoginWay() : loginWay);
        request.setAttribute("path", properties.getPath());
        request.setAttribute("loginPage", properties.getPage().getLoginPage());
        return "login-page";
    }

    @GetMapping("/home")
    public String home(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setAttribute("path", properties.getPath());
        request.setAttribute("loginPage", properties.getPage().getLoginPage());
        return "home-page";
    }

    @GetMapping("/permission")
    public void permission(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!rbacService.hasPermission(request, SecurityContextHolder.getContext().getAuthentication())) {
            response.sendError(HttpStatus.FORBIDDEN.value());
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            SecurityUserInfo userInfo = (SecurityUserInfo) authentication.getPrincipal();
            response.setHeader("login_id", userInfo.getLoginId());
            response.setHeader("login_name", userInfo.getName());
        }
    }
}
