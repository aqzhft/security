package cc.powind.security.core.login;

import cc.powind.security.core.properties.SecurityProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class SecurityEntrypoint {

    @Autowired
    private SecurityProperties properties;

    @GetMapping("/authentication/require")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String login(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/login-page.html");
//        requestDispatcher.forward(request, response);
        String loginWay = request.getParameter("way");
        request.setAttribute("loginWay", StringUtils.isBlank(loginWay) ? properties.getPage().getLoginPage().getLoginWay() : loginWay);
        request.setAttribute("basePath", properties.getBasePath());
        request.setAttribute("homePage", properties.getHomePage());
        request.setAttribute("loginPage", properties.getPage().getLoginPage());
        return "login-page";
    }

    @GetMapping("/home")
    public void mainPage(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/home-page.html");
        requestDispatcher.forward(request, response);
    }
}
