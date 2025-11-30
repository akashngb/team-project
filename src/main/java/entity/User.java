package entity;

import java.util.HashMap;
import java.util.Map;

/**
 * An entity representing a user. Users have a username, password and highscores across games.
 */
public class User {

    private final String name;
    private final String password;
    private final Map<String, Integer> highscores;

    /**
     * Creates a new user with the given params.
     * @param name the username
     * @param password the password
     * @param highscores the map {gameName -> highscore} for each game
     * @throws IllegalArgumentException if any params are empty
     */
    public User(String name, String password, Map<String, Integer> highscores) {
        if ("".equals(name)) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if ("".equals(password)) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        for (Map.Entry<String, Integer> entry : highscores.entrySet()) {
            // Test if all game names are non-empty
            if (entry.getKey() == null || "".equals(entry.getKey())) {
                throw new IllegalArgumentException("Game name in highscores cannot be empty");
            }
            // Test if all scores are non-negative
            if (entry.getValue() == null || entry.getValue() < 0) {
                throw new IllegalArgumentException("Highscore for game " + entry.getKey() + " cannot be null or negative");
            }
            // Test if all game names are valid
            try {
                Games.valueOf(entry.getKey());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid game name in highscores: " + entry.getKey());
            }
        }
        this.name = name;
        this.password = password;
        this.highscores = highscores;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public Map<String, Integer> getHighscores() { return highscores; }
}
