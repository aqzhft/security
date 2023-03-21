package cc.powind.security.token.service;

import cc.powind.security.token.exception.TokenInvalidException;
import cc.powind.security.token.model.Token;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface TokenService<T extends Token> {

    void create(HttpServletRequest request, HttpServletResponse response) throws IOException;

    void validate(HttpServletRequest request) throws TokenInvalidException;
}
