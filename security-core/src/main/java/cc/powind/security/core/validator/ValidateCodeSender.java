package cc.powind.security.core.validator;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@FunctionalInterface
public interface ValidateCodeSender {

    /**
     * send validate code to client
     *
     * @param validateCode validate code
     * @param response servlet response
     * @throws IOException io exception
     */
    default void send(ValidateCode validateCode, HttpServletResponse response) throws IOException {
        send(validateCode, null, response);
    }

    /**
     * send validate code to client in a specific way
     *
     * @param validateCode validate code
     * @param types multiple way send message description
     * @param response response
     * @throws IOException io exception
     */
    void send(ValidateCode validateCode, String[] types, HttpServletResponse response) throws IOException;
}
