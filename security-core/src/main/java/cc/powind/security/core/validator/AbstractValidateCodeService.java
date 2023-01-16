package cc.powind.security.core.validator;

import cc.powind.security.core.exception.ValidateCodeException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractValidateCodeService <T extends ValidateCode> implements ValidateCodeService <T>, InitializingBean {

    protected Log log = LogFactory.getLog(getClass());

    private final List<Request> interceptRequestList = new ArrayList<>();

    protected final String DEFAULT_VALIDATE_CODE_PARAMETER_LEN = "len";

    protected final String DEFAULT_VALIDATE_PARAMETER_SESSION_ID = "sessionId";

    private String validateCodeParameterName = "validateCode";

    private PathMatcher pathMatcher = new AntPathMatcher();

    private String validateCodeParameterLen = DEFAULT_VALIDATE_CODE_PARAMETER_LEN;

    private String validateCodeParameterSessionId = DEFAULT_VALIDATE_PARAMETER_SESSION_ID;

    private int len = 4;

    private long timeout = 300;

    private List<String> interceptUrls;

    private ValidateCodeRepository validateCodeRepository;

    public String getValidateCodeParameterSessionId() {
        return validateCodeParameterSessionId;
    }

    public void setValidateCodeParameterSessionId(String validateCodeParameterSessionId) {
        this.validateCodeParameterSessionId = validateCodeParameterSessionId;
    }

    public String getValidateCodeParameterName() {
        return validateCodeParameterName;
    }

    public void setValidateCodeParameterName(String validateCodeParameterName) {
        this.validateCodeParameterName = validateCodeParameterName;
    }

    public String getValidateCodeParameterLen() {
        return validateCodeParameterLen;
    }

    public void setValidateCodeParameterLen(String validateCodeParameterLen) {
        this.validateCodeParameterLen = validateCodeParameterLen;
    }

    public PathMatcher getPathMatcher() {
        return pathMatcher;
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
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

    public ValidateCodeRepository getValidateCodeRepository() {
        return validateCodeRepository;
    }

    public void setValidateCodeRepository(ValidateCodeRepository validateCodeRepository) {
        this.validateCodeRepository = validateCodeRepository;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

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
            Assert.isTrue(split.length == 2, "校验码拦截地址错误！");

            this.interceptRequestList.add(new Request(split[1], split[0]));
        }
    }

    @Override
    public T create(@Nullable ServletWebRequest webRequest) {

        // 1、创建校验码
        T code = doCreate(webRequest);

        // 2、将创建好的校验码存入容器中
        save(code);

        // 3、发送校验码
        send(code, webRequest);

        return code;
    }

    @Override
    public void validate(ServletWebRequest webRequest) throws ValidateCodeException {

        // 1、判断是否需要校验
        if (!checkIntercept(webRequest)) {
            return;
        }

        // 2、校验request中的validateCode
        ValidateCode requestCode = getValidateCodeFromRequest(webRequest);
        if (requestCode == null || requestCode.getCode() == null || "".equals(requestCode.getCode())) {
            throw new ValidateCodeException("请求中未提交校验码！");
        }

        // 3、从服务器中拿到validateCode
        ValidateCode validateCode = getValidateCode(webRequest);

        // 4、服务器中的validateCode是否存在
        if (validateCode == null) {
            throw new ValidateCodeException("校验码不存在！");
        }

        // 5、服务器中的validateCode是否过期
        if (validateCode.expired()) {
            throw new ValidateCodeException("校验码已经过期！");
        }

        // 6、判断两个校验码是否匹配
        if (!validateCode.isEqual(requestCode)) {
            throw new ValidateCodeException("校验码不匹配！");
        }

        // 7、从服务器中删除校验码
        removeValidateCode(validateCode);
    }

    /**
     * 检验是否需要检验当前请求
     *
     * @param request 请求
     * @return bool
     */
    protected boolean checkIntercept(ServletWebRequest request) {

        if (interceptRequestList.isEmpty()) {
            return false;
        }

        String uri = request.getRequest().getRequestURI();
        String method = request.getRequest().getMethod();

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
    protected String getValidateCodeId(ServletWebRequest request) {
        String sessionId = request.getParameter(this.validateCodeParameterSessionId);

        if (sessionId == null) {
            sessionId = request.getRequest().getSession().getId();
        }

        return (getClass().getSimpleName()) + "-" + sessionId;
    }

    /**
     * 从容器中获取校验码
     *
     * @param webRequest 请求
     * @return 校验码
     */
    protected ValidateCode getValidateCode(ServletWebRequest webRequest) {
        return validateCodeRepository.get(getValidateCodeId(webRequest));
    }

    /**
     * 从请求中提取校验码信息
     *
     * @param webRequest 请求
     * @return 校验码
     */
    protected ValidateCode getValidateCodeFromRequest(ServletWebRequest webRequest) {
        String code = webRequest.getRequest().getParameter(validateCodeParameterName);
        return new BaseValidateCode() {
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
    protected void removeValidateCode(ValidateCode code) {
        validateCodeRepository.remove(code.getSessionId());
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
    protected void save(ValidateCode code) {
        validateCodeRepository.save(code);
    }

    /**
     * 生成校验码
     *
     * @param webRequest 请求
     * @return 校验码
     */
    protected abstract T doCreate(ServletWebRequest webRequest);

    /**
     * 发送校验码
     *
     * @param code 校验码
     * @param request request and response
     */
    protected abstract void send(T code, ServletWebRequest request);

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
