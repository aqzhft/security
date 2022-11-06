package cc.powind.security.core.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ValidateCodeEntryPoint {

    private static final String VALIDATE_CODE_SERVICE_SUFFIX = "CodeService";

    private Map<String, ValidateCodeService<? extends ValidateCode>> serviceMap = new HashMap<>();

    public Map<String, ValidateCodeService<? extends ValidateCode>> getServiceMap() {
        return serviceMap;
    }

    @Autowired
    public void setServiceMap(Map<String, ValidateCodeService<? extends ValidateCode>> serviceMap) {
        this.serviceMap = serviceMap;
    }

    @GetMapping("/code/{type}")
    public void code(@PathVariable String type, HttpServletRequest request, HttpServletResponse response) throws IOException {

        ValidateCodeService<? extends ValidateCode> validateCodeService = serviceMap.get(type + VALIDATE_CODE_SERVICE_SUFFIX);
        Assert.notNull(validateCodeService, "未查询到校验码实现程序，请检查校验码请求链接！");

        validateCodeService.create(new ServletWebRequest(request, response));
    }
}