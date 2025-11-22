package data_access.wordle;

import use_case.wordle.GameSessionGateway;
import entity.wordle.WordleGame;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple thread-safe in-memory session store keyed by userId.
 * Good for development and unit tests.
 */
public class InMemoryGameSessionGateway implements GameSessionGateway {
    private final Map<String, WordleGame> store = new ConcurrentHashMap<>();

    @Override
    public void save(String userId, WordleGame game) {
        if (userId == null) throw new IllegalArgumentException("userId required");
        store.put(userId, game);
    }

    @Override
    public WordleGame load(String userId) {
        if (userId == null) return null;
        return store.get(userId);
    }

    @Override
    public void remove(String userId) {
        if (userId == null) return;
        store.remove(userId);
    }
}
