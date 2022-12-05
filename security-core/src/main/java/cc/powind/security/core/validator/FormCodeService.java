package cc.powind.security.core.validator;

import org.springframework.web.context.request.ServletWebRequest;

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
            request.getResponse().getWriter().print(code);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
