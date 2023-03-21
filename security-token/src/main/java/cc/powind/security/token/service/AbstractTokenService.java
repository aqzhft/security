package cc.powind.security.token.service;

import cc.powind.security.token.TokenRepository;
import cc.powind.security.token.exception.TokenInvalidException;
import cc.powind.security.token.model.AbstractToken;
import cc.powind.security.token.model.Token;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTokenService<T extends Token> implements TokenService<T> {

    protected Log log = LogFactory.getLog(getClass());

    private final PathMatcher pathMatcher = new AntPathMatcher();

    private final List<Request> interceptRequestList = new ArrayList<>();

    protected final String DEFAULT_TOKEN_PARAMETER_LEN = "len";

    protected final String DEFAULT_TOKEN_PARAMETER_SESSION_ID = "sessionId";

    private String tokenParameterName = "validateCode";

    private String tokenParameterLen = DEFAULT_TOKEN_PARAMETER_LEN;

    private String tokenParameterSessionId = DEFAULT_TOKEN_PARAMETER_SESSION_ID;

    private String tokenParameterApplyId = "tokenApplyId";

    private int len = 4;

    private long timeout = 300;

    private List<String> interceptUrls;

    private TokenRepository tokenRepository;

    public String getTokenParameterName() {
        return tokenParameterName;
    }

    public void setTokenParameterName(String tokenParameterName) {
        this.tokenParameterName = tokenParameterName;
    }

    public String getTokenParameterLen() {
        return tokenParameterLen;
    }

    public void setTokenParameterLen(String tokenParameterLen) {
        this.tokenParameterLen = tokenParameterLen;
    }

    public String getTokenParameterSessionId() {
        return tokenParameterSessionId;
    }

    public void setTokenParameterSessionId(String tokenParameterSessionId) {
        this.tokenParameterSessionId = tokenParameterSessionId;
    }

    public String getTokenParameterApplyId() {
        return tokenParameterApplyId;
    }

    public void setTokenParameterApplyId(String tokenParameterApplyId) {
        this.tokenParameterApplyId = tokenParameterApplyId;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public List<String> getInterceptUrls() {
        return interceptUrls;
    }

    public void setInterceptUrls(List<String> interceptUrls) {
        this.interceptUrls = interceptUrls;
    }

    public TokenRepository getTokenRepository() {
        return tokenRepository;
    }

    public void setTokenRepository(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public void init() {
        this.initInterceptRequestList();
    }

    /**
     * 初始化拦截地址
     */
    private void initInterceptRequestList() {

        if (interceptUrls == null || interceptUrls.isEmpty()) {
            return;
        }

        for (String url: interceptUrls) {
            String[] split = url.split(":");
            if (split.length != 2) {
                throw new IllegalArgumentException("校验码拦截地址错误");
            }

            this.interceptRequestList.add(new Request(split[1], split[0]));
        }
    }

    @Override
    public void create(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 1、创建校验码
        T code = doCreate(request);

        // 2、将创建好的校验码存入容器中
        save(code);

        // 3、发送校验码
        send(code, request, response);
    }

    @Override
    public void validate(HttpServletRequest request) throws TokenInvalidException {

        // 1、whether to verify the request
        if (!checkIntercept(request)) {
            return;
        }

        // 2、get token from request
        Token requestToken = getTokenFromRequest(request);
        if (requestToken == null || requestToken.getCode() == null || "".equals(requestToken.getCode())) {
            throw new TokenInvalidException("请求中未提交校验码！");
        }

        // 3、get all tokens from server
        Token[] tokens = getTokenFromServer(request);

        // 4、服务器中的validateCode是否存在
        if (tokens == null || tokens.length == 0) {
            throw new TokenInvalidException("校验码不存在！");
        }

        // 6、判断两个校验码是否匹配
        Token token = detectToken(tokens, requestToken);
        if (token == null) {
            throw new TokenInvalidException("校验码不匹配！");
        }

        // 6、服务器中的validateCode是否过期
        if (token.expired()) {
            throw new TokenInvalidException("校验码已经过期！");
        }

        // 7、从服务器中删除校验码
        removeValidateCode(token);
    }

    private Token detectToken(Token[] tokens, Token requestToken) {
        for (Token token : tokens) {
            if (token.isEqual(requestToken)) {
                return token;
            }
        }
        return null;
    }

    /**
     * 检验是否需要检验当前请求
     *
     * @param request 请求
     * @return bool
     */
    protected boolean checkIntercept(HttpServletRequest request) {

        if (interceptRequestList.isEmpty()) {
            return false;
        }

        String uri = request.getRequestURI();
        String method = request.getMethod();

        for (Request requestPath: interceptRequestList) {
            if (method.equalsIgnoreCase(requestPath.getMethod()) && pathMatcher.match(requestPath.getUri(), uri)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 从请求中获取唯一ID
     *
     * @param request 请求
     * @return 校验码唯一ID
     */
    protected String getValidateCodeId(HttpServletRequest request) {
        String sessionId = request.getParameter(this.tokenParameterSessionId);

        if (sessionId == null) {
            sessionId = request.getSession().getId();
        }

        return sessionId;
    }

    /**
     * 从容器中获取校验码
     *
     * @param request 请求
     * @return 校验码
     */
    protected Token[] getTokenFromServer(HttpServletRequest request) {
        return tokenRepository.get(getValidateCodeId(request));
    }

    /**
     * 从请求中提取校验码信息
     *
     * @param request 请求
     * @return 校验码
     */
    protected Token getTokenFromRequest(HttpServletRequest request) {
        String code = request.getParameter(tokenParameterName);
        return new AbstractToken() {
            @Override
            public String getCode() {
                return code;
            }
        };
    }

    /**
     * 从容器中删除校验码
     *
     * @param code 校验码
     */
    protected void removeValidateCode(Token code) {
        tokenRepository.remove(code);
    }

    /**
     * 创建随机码
     *
     * @param len 长度
     * @return 随机码
     */
    protected String createCode(int len) {
        return RandomStringUtils.randomNumeric(len);
    }

    /**
     * 保存校验码
     *
     * @param code 校验码
     */
    protected void save(Token code) {
        tokenRepository.save(code);
    }

    /**
     * 生成校验码
     *
     * @param request 请求
     * @return 校验码
     */
    protected abstract T doCreate(HttpServletRequest request);

    /**
     * 发送校验码
     *
     * @param code 校验码
     * @param request request and response
     */
    protected abstract void send(T code, HttpServletRequest request, HttpServletResponse response) throws IOException;

    protected String getApplyId(HttpServletRequest request) {
        return request.getParameter(tokenParameterApplyId);
    }

    protected String getCode(HttpServletRequest request) {
        return request.getParameter(getTokenParameterName());
    }

    protected int getDefaultLen(HttpServletRequest request) {
        String tokenValueLength = request.getParameter(getTokenParameterLen());

        if (StringUtils.isBlank(tokenValueLength) || !NumberUtils.isDigits(tokenValueLength)) {
            return getLen();
        }

        return NumberUtils.toInt(tokenValueLength);
    }

    private static class Request {

        private String method;

        private String uri;

        public Request(String method, String uri) {
            this.method = method;
            this.uri = uri;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }
    }
}
