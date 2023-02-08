package cc.powind.security.basic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

@RestController
@SpringBootApplication
public class SecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityApplication.class, args);
    }

    @Autowired
    private RequestMappingHandlerAdapter adapter;

    @GetMapping("")
    public String hello(String loginId) {
        SecurityContext context = SecurityContextHolder.getContext();
        System.out.println(context);

        System.out.println("current login id: " + loginId);

        return "hello world";
    }
}
