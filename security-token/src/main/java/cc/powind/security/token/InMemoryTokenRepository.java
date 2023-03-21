package cc.powind.security.token;

import cc.powind.security.token.model.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存实现
 */
public class InMemoryTokenRepository implements TokenRepository {

    private final static Map<String, List<Token>> cache = new ConcurrentHashMap<>();

    @Override
    public void save(Token code) {
        cache.putIfAbsent(code.getSessionId(), new ArrayList<>());
        cache.get(code.getSessionId()).add(code);
    }

    @Override
    public void remove(Token token) {
        List<Token> tokens = cache.get(token.getSessionId());
        if (tokens != null && token.getApplyId() != null) {
            tokens.removeIf(next -> token.getApplyId().equals(next.getApplyId()));
        }
    }

    @Override
    public Token[] get(String sessionId) {
        List<Token> tokens = cache.get(sessionId);
        return tokens == null ? null : cache.get(sessionId).toArray(new Token[0]);
    }
}
