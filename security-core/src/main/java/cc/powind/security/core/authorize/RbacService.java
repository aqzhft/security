package cc.powind.security.core.authorize;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface RbacService {

	boolean hasPermission(HttpServletRequest request, Authentication authentication);
}
