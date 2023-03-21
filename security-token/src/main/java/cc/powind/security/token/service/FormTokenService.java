package cc.powind.security.token.service;

import cc.powind.security.token.model.FormToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class FormTokenService extends AbstractTokenService<FormToken> {

    @Override
    protected FormToken doCreate(HttpServletRequest request) {
        return new FormToken(getApplyId(request), getValidateCodeId(request), UUID(), getTimeout());
    }

    private String UUID() {
        return UUID.randomUUID().toString().toLowerCase().replaceAll("-", "");
    }

    @Override
    protected void send(FormToken code, HttpServletRequest request, HttpServletResponse response) {
        try {
            response.getWriter().print(code.getCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
