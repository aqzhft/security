package cc.powind.security.token.service;

import cc.powind.security.token.model.Token;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface TokenNotifier {

    void send(Token token, HttpServletRequest request, HttpServletResponse response) throws IOException;
}
