package cc.powind.security.core.validator;

import cc.powind.security.core.exception.ValidateCodeException;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractValidateCodeService <T extends ValidateCode> implements ValidateCodeService <T>, InitializingBean {

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
     * ?????????????????????
     */
    private void initInterceptRequestList() {

        if (interceptUrls == null || interceptUrls.isEmpty()) {
            return;
        }

        for (String url: interceptUrls) {
            String[] split = url.split(":");
            Assert.isTrue(split.length == 2, "??????????????????????????????");

            this.interceptRequestList.add(new Request(split[1], split[0]));
        }
    }

    @Override
    public T create(@Nullable ServletWebRequest webRequest) {

        // 1??????????????????
        T code = doCreate(webRequest);

        // 2??????????????????????????????????????????
        save(code);

        // 3??????????????????
        send(code, webRequest);

        return code;
    }

    @Override
    public void validate(ServletWebRequest webRequest) throws ValidateCodeException {

        // 1???????????????????????????
        if (!checkIntercept(webRequest)) {
            return;
        }

        // 2?????????request??????validateCode
        ValidateCode requestCode = getValidateCodeFromRequest(webRequest);
        if (requestCode == null || requestCode.getCode() == null || "".equals(requestCode.getCode())) {
            throw new ValidateCodeException("??????????????????????????????");
        }

        // 3????????????????????????validateCode
        ValidateCode validateCode = getValidateCode(webRequest);

        // 4??????????????????validateCode????????????
        if (validateCode == null) {
            throw new ValidateCodeException("?????????????????????");
        }

        // 5??????????????????validateCode????????????
        if (validateCode.expired()) {
            throw new ValidateCodeException("????????????????????????");
        }

        // 6????????????????????????????????????
        if (!validateCode.isEqual(requestCode)) {
            throw new ValidateCodeException("?????????????????????");
        }

        // 7?????????????????????????????????
        removeValidateCode(validateCode);
    }

    /**
     * ????????????????????????????????????
     *
     * @param request ??????
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
     * ????????????????????????ID
     *
     * @param request ??????
     * @return ???????????????ID
     */
    protected String getValidateCodeId(ServletWebRequest request) {
        String sessionId = request.getParameter(this.validateCodeParameterSessionId);

        if (sessionId == null) {
            sessionId = request.getRequest().getSession().getId();
        }

        return (getClass().getSimpleName()) + "-" + sessionId;
    }

    /**
     * ???????????????????????????
     *
     * @param webRequest ??????
     * @return ?????????
     */
    protected ValidateCode getValidateCode(ServletWebRequest webRequest) {
        return validateCodeRepository.get(getValidateCodeId(webRequest));
    }

    /**
     * ?????????????????????????????????
     *
     * @param webRequest ??????
     * @return ?????????
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
     * ???????????????????????????
     *
     * @param code ?????????
     */
    protected void removeValidateCode(ValidateCode code) {
        validateCodeRepository.remove(code.getSessionId());
    }

    /**
     * ???????????????
     *
     * @param len ??????
     * @return ?????????
     */
    protected String createCode(int len) {
        return RandomStringUtils.randomNumeric(len);
    }

    /**
     * ???????????????
     *
     * @param code ?????????
     */
    protected void save(ValidateCode code) {
        validateCodeRepository.save(code);
    }

    /**
     * ???????????????
     *
     * @param webRequest ??????
     * @return ?????????
     */
    protected abstract T doCreate(ServletWebRequest webRequest);

    /**
     * ???????????????
     *
     * @param code ?????????
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
