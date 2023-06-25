package cc.powind.security.core.login;

import cc.powind.security.core.authorize.RbacService;
import cc.powind.security.core.properties.PageProperties;
import cc.powind.security.core.properties.PathProperties;
import cc.powind.security.core.properties.WxworkProperties;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping
public class SecurityEntrypoint {

    private PathProperties path;

    private PageProperties page;

    private RbacService rbacService;

    private ClientRegistrationRepository repository;

    private WxworkProperties wxworkProperties;

    private String[] showLoginWays;

    private LoginInfoService loginInfoService;

    private PasswordEncoder passwordEncoder;

    public PathProperties getPath() {
        return path;
    }

    public void setPath(PathProperties path) {
        this.path = path;
    }

    public PageProperties getPage() {
        return page;
    }

    public void setPage(PageProperties page) {
        this.page = page;
    }

    public RbacService getRbacService() {
        return rbacService;
    }

    public void setRbacService(RbacService rbacService) {
        this.rbacService = rbacService;
    }

    public ClientRegistrationRepository getRepository() {
        return repository;
    }

    public void setRepository(ClientRegistrationRepository repository) {
        this.repository = repository;
    }

    public WxworkProperties getWxworkProperties() {
        return wxworkProperties;
    }

    public void setWxworkProperties(WxworkProperties wxworkProperties) {
        this.wxworkProperties = wxworkProperties;
    }

    public String[] getShowLoginWays() {
        return showLoginWays;
    }

    public void setShowLoginWays(String[] showLoginWays) {
        this.showLoginWays = showLoginWays;
    }

    public LoginInfoService getLoginInfoService() {
        return loginInfoService;
    }

    public void setLoginInfoService(LoginInfoService loginInfoService) {
        this.loginInfoService = loginInfoService;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String login(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String loginWay = request.getParameter("way");
        request.setAttribute("loginWay", StringUtils.isBlank(loginWay) ? page.getLoginPage().getLoginWay() : loginWay);
        request.setAttribute("path", path);
        request.setAttribute("loginPage", page.getLoginPage());

        List<LoginWay> loginWayList = getLoginWayList(wxworkProperties, repository);

        loginWayList = loginWayList.stream().filter(way -> ArrayUtils.isNotEmpty(showLoginWays) && ArrayUtils.contains(showLoginWays, way.getName())).collect(Collectors.toList());

        request.setAttribute("loginWays", loginWayList);

        if (ArrayUtils.contains(showLoginWays, "verify")) {
            request.setAttribute("verifyLoginWay", 1);
        }

        return "login-page";
    }

    private List<LoginWay> getLoginWayList(WxworkProperties wxworkProperties, ClientRegistrationRepository repository) {

        List<LoginWay> loginWays = new ArrayList<>();

        if (StringUtils.isNotBlank(wxworkProperties.getAuthorizationUri())) {
            loginWays.add(new LoginWay("wxwork", "wxwork", "/wxwork/oauth2/authorization"));
        }

        if (StringUtils.isNotBlank(wxworkProperties.getAuthorizationQrcodeUri())) {
            loginWays.add(new LoginWay("wxworkQrcode", "wxwork", "/wxwork/oauth2/authorization?type=code"));
        }

        if (repository != null && repository.findByRegistrationId("gitlab") != null) {
            loginWays.add(new LoginWay("gitlab", "gitlab", "/oauth2/authorization/gitlab"));
        }

        if (repository != null && repository.findByRegistrationId("github") != null) {
            loginWays.add(new LoginWay("github", "github", "/oauth2/authorization/github"));
        }

        loginWays.add(new LoginWay("mobile", "smsCode", path.getLoginPage() + "?way=mobile"));
        loginWays.add(new LoginWay("email", "mailbox", path.getLoginPage() + "?way=email"));

        return loginWays;
    }

    @GetMapping("/home")
    public String home(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setAttribute("path", path);
        request.setAttribute("loginPage", page.getLoginPage());
        return "home-page";
    }

    @GetMapping("/password")
    public String password(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setAttribute("path", path);
        request.setAttribute("loginPage", page.getLoginPage());
        return "pwd-page";
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

    @ResponseBody
    @PostMapping("/password")
    public void resetPassword(Authentication authentication, String oldPassword, String newPassword, HttpServletResponse response) throws IOException {

        try {
            // 查询用户信息
            SecurityUserInfo userInfo = (SecurityUserInfo) authentication.getPrincipal();

            // 判断旧密码是否正确
            if (!passwordEncoder.matches(oldPassword, userInfo.getPassword())) {
                throw new BadCredentialsException("旧密码输入不正确");
            }

            if (StringUtils.isBlank(newPassword)) {
                throw new BadCredentialsException("新密码不能为空");
            }

            // 设置新密码
            loginInfoService.updatePassword(userInfo.getLoginId(), passwordEncoder.encode(newPassword));
        } catch (AuthenticationException e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(e.getMessage());
        }
    }

    public static class LoginWay {

        private String name;

        private String icon;

        private String uri;

        public LoginWay(String name, String icon, String uri) {
            this.name = name;
            this.icon = icon;
            this.uri = uri;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }
    }
}
