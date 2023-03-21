package cc.powind.security.token;

import cc.powind.security.token.model.Token;

/**
 * 校验码存储器
 */
public interface TokenRepository {

    void save(Token code);

    void remove(Token token);

    Token[] get(String sessionId);
}
