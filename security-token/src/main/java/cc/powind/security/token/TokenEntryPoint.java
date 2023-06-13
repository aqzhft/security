package cc.powind.security.token;

import cc.powind.security.token.model.Token;
import cc.powind.security.token.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequestMapping
public class TokenEntryPoint {

    private static final String VALIDATE_CODE_SERVICE_SUFFIX = "TokenService";

    private Map<String, TokenService<? extends Token>> serviceMap = new HashMap<>();

    public Map<String, TokenService<? extends Token>> getServiceMap() {
        return serviceMap;
    }

    @Autowired
    public void setServiceMap(Map<String, TokenService<? extends Token>> serviceMap) {
        this.serviceMap = serviceMap;
    }

    @GetMapping("/code/{type}")
    public void code(@PathVariable String type, HttpServletRequest request, HttpServletResponse response) throws IOException {

        TokenService<? extends Token> validateCodeService = serviceMap.get(type + VALIDATE_CODE_SERVICE_SUFFIX);
        Assert.notNull(validateCodeService, "未查询到校验码实现程序，请检查校验码请求链接！");

        validateCodeService.create(request, response);
    }
}