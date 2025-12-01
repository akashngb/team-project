package entity;

import java.util.Map;

/**
 * Factory for creating CommonUser objects.
 */
public class UserFactory {
    public User create(String name, String password, Map<String, Integer> highscores) {
        return new User(name, password, highscores);
    }
}
