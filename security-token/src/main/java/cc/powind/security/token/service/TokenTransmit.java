package cc.powind.security.token.service;

import cc.powind.security.token.model.Token;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@FunctionalInterface
public interface TokenTransmit {

    /**
     * send validate code to client
     *
     * @param token validate code
     * @param response servlet response
     * @throws IOException io exception
     */
    default void send(Token token, HttpServletResponse response) throws IOException {
        send(token, null, response);
    }

    /**
     * send validate code to client in a specific way
     *
     * @param token validate code
     * @param types multiple way send message description
     * @param response response
     * @throws IOException io exception
     */
    void send(Token token, String[] types, HttpServletResponse response) throws IOException;
}
