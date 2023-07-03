package cc.powind.security.assembly.config;

import cc.powind.security.core.proxy.RequestParameterEnum;
import org.springframework.core.log.LogMessage;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.security.web.util.matcher.*;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomRequestCache extends HttpSessionRequestCache {

    private boolean createSessionAllowed = true;

    private RequestMatcher requestMatcher;

    private String sessionAttrName = "SPRING_SECURITY_SAVED_REQUEST";

    private boolean checkIfNginxAuthRequestCheck(HttpServletRequest request) {
        String headerNginx = request.getHeader(RequestParameterEnum.NGINX_AUTH.getValue());
        return  "1".equals(headerNginx);
    }

    @Override
    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {

        if (checkIfNginxAuthRequestCheck(request)) {

            if (!this.requestMatcher.matches(request)) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace(
                            LogMessage.format("Did not save request since it did not match [%s]", this.requestMatcher));
                }
                return;
            }
            if (this.createSessionAllowed || request.getSession(false) != null) {

                String scheme = request.getHeader(RequestParameterEnum.SCHEME.getValue());
                String host = request.getHeader(RequestParameterEnum.HOST.getValue());
                String requestURI = request.getHeader(RequestParameterEnum.URI.getValue());
                String serverPort = request.getHeader(RequestParameterEnum.SERVER_PORT.getValue());

                String originalURI = UrlUtils.buildFullRequestUrl(scheme, host, Integer.parseInt(serverPort), requestURI, null);

                UriComponents uriComponents = UriComponentsBuilder.fromUriString(originalURI).build();
                DefaultSavedRequest.Builder builder = new DefaultSavedRequest.Builder();
                int port = getPort(uriComponents);
                SavedRequest savedRequest = builder.setScheme(uriComponents.getScheme()).setServerName(uriComponents.getHost())
                        .setRequestURI(uriComponents.getPath()).setQueryString(uriComponents.getQuery()).setServerPort(port)
                        .setMethod(request.getMethod()).build();

                request.getSession().setAttribute(this.sessionAttrName, savedRequest);
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug(LogMessage.format("Saved request %s to session", savedRequest.getRedirectUrl()));
                }
            }
            else {
                this.logger.trace("Did not save request since there's no session and createSessionAllowed is false");
            }

        } else {
            super.saveRequest(request, response);
        }
    }

    @Override
    public void setRequestMatcher(RequestMatcher requestMatcher) {
        super.setRequestMatcher(requestMatcher);
        this.requestMatcher = requestMatcher;
    }

    public RequestMatcher getRequestMatcher() {
        return requestMatcher;
    }

    @Override
    public void setSessionAttrName(String sessionAttrName) {
        super.setSessionAttrName(sessionAttrName);
        this.sessionAttrName = sessionAttrName;
    }

    public String getSessionAttrName() {
        return sessionAttrName;
    }

    public boolean isCreateSessionAllowed() {
        return createSessionAllowed;
    }

    @Override
    public void setCreateSessionAllowed(boolean createSessionAllowed) {
        super.setCreateSessionAllowed(createSessionAllowed);
        this.createSessionAllowed = createSessionAllowed;
    }

    public void initRequestMatcher(HttpSecurity http) {
        this.requestMatcher = createDefaultSavedRequestMatcher(http);
        super.setRequestMatcher(this.requestMatcher);
    }

    private RequestMatcher createDefaultSavedRequestMatcher(HttpSecurity http) {
        RequestMatcher notFavIcon = new NegatedRequestMatcher(new AntPathRequestMatcher("/**/favicon.*"));
        RequestMatcher notXRequestedWith = new NegatedRequestMatcher(
                new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest"));
        boolean isCsrfEnabled = http.getConfigurer(CsrfConfigurer.class) != null;
        List<RequestMatcher> matchers = new ArrayList<>();
        if (isCsrfEnabled) {
            RequestMatcher getRequests = new AntPathRequestMatcher("/**", "GET");
            matchers.add(0, getRequests);
        }
        matchers.add(notFavIcon);
        matchers.add(notMatchingMediaType(http, MediaType.APPLICATION_JSON));
        matchers.add(notXRequestedWith);
        matchers.add(notMatchingMediaType(http, MediaType.MULTIPART_FORM_DATA));
        matchers.add(notMatchingMediaType(http, MediaType.TEXT_EVENT_STREAM));
        return new AndRequestMatcher(matchers);
    }

    private RequestMatcher notMatchingMediaType(HttpSecurity http, MediaType mediaType) {
        ContentNegotiationStrategy contentNegotiationStrategy = http.getSharedObject(ContentNegotiationStrategy.class);
        if (contentNegotiationStrategy == null) {
            contentNegotiationStrategy = new HeaderContentNegotiationStrategy();
        }
        MediaTypeRequestMatcher mediaRequest = new MediaTypeRequestMatcher(contentNegotiationStrategy, mediaType);
        mediaRequest.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));
        return new NegatedRequestMatcher(mediaRequest);
    }

    private int getPort(UriComponents uriComponents) {
        int port = uriComponents.getPort();
        if (port != -1) {
            return port;
        }
        if ("https".equalsIgnoreCase(uriComponents.getScheme())) {
            return 443;
        }
        return 80;
    }
}
