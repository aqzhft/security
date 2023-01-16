package cc.powind.security.core.validator;

import org.springframework.util.Assert;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class FormCodeService extends AbstractValidateCodeService<FormCode> {

    @Override
    protected FormCode doCreate(ServletWebRequest webRequest) {
        return new FormCode(getValidateCodeId(webRequest), UUID(), getTimeout());
    }

    private String UUID() {
        return UUID.randomUUID().toString().toLowerCase().replaceAll("-", "");
    }

    @Override
    protected void send(FormCode code, ServletWebRequest request) {
        try {

            HttpServletResponse response = request.getResponse();
            Assert.notNull(response, "response must not be null");

            request.getResponse().getWriter().print(code.getCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
