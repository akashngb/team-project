package data_access;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import entity.User;
import use_case.change_password.ChangePasswordUserDataAccessInterface;
import use_case.leaderboard.LeaderBoardDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;
import use_case.logout.LogoutUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO for user data implemented using a JSON File to persist the data.
 * Using JSON allows to use mappings of all kinds
 * like the Map<String, Integer> highscores field.
 */
public class FileUserDataAccessObject implements SignupUserDataAccessInterface,
                                                 LoginUserDataAccessInterface,
                                                 ChangePasswordUserDataAccessInterface,
                                                 LogoutUserDataAccessInterface,
                                                 LeaderBoardDataAccessInterface {

    private final File jsonFile;
    private final Map<String, User> accounts = new HashMap<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create(); // Pretty printing for readable JSON

    private String currentUsername;

    /**
     * Construct this DAO for saving to and reading from a local JSON file.
     * If the file exists, it will load existing user data. Otherwise, it creates a new empty file.
     *
     * @param jsonPath the path of the JSON file to save to
     * @throws RuntimeException if there is an exception when accessing the file
     */
    public FileUserDataAccessObject(String jsonPath) {
        this.jsonFile = new File(jsonPath);

        try {
            // If file exists and has content, load the existing data
            if (jsonFile.exists() && jsonFile.length() > 0) {
                load();
            } else {
                // Initialize with empty JSON object
                save();
            }
        } catch (RuntimeException ex) {
            // Re-throw with more context about what failed
            throw new RuntimeException("Failed to initialize FileUserDataAccessObject with file: " + jsonPath, ex);
        }
    }

    /**
     * Load user data from the JSON file.
     * Deserializes the JSON directly into a Map of username to User objects.
     *
     * @throws RuntimeException if there is an IOException when reading the file
     */
    private void load() {
        try (Reader reader = new FileReader(jsonFile)) {
            // Define the type for Gson to deserialize into
            Type type = new TypeToken<Map<String, User>>() {
            }.getType();
            Map<String, User> loadedAccounts = gson.fromJson(reader, type);

            // Add all loaded users to the accounts map
            if (loadedAccounts != null) {
                accounts.putAll(loadedAccounts);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Save all user accounts to the JSON file.
     * Serializes the User objects directly to JSON.
     *
     * @throws RuntimeException if there is an IOException when writing to the file
     */
    private void save() {
        try (Writer writer = new FileWriter(jsonFile)) {
            // Write the accounts map directly to JSON with pretty printing
            gson.toJson(accounts, writer);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Save a user to the accounts map and persist to file.
     *
     * @param user the user to save
     */
    @Override
    public void save(User user) {
        accounts.put(user.getName(), user);
        this.save();
    }

    /**
     * Retrieve a user by username.
     *
     * @param username the username to look up
     * @return the User object, or null if not found
     */
    @Override
    public User get(String username) {
        return accounts.get(username);
    }

    /**
     * Set the current logged-in username.
     *
     * @param name the username of the currently logged-in user
     */
    @Override
    public void setCurrentUsername(String name) {
        currentUsername = name;
    }

    /**
     * Get the current logged-in username.
     *
     * @return the username of the currently logged-in user
     */
    @Override
    public String getCurrentUsername() {
        return currentUsername;
    }

    /**
     * Check if a user exists by username.
     *
     * @param identifier the username to check
     * @return true if the user exists, false otherwise
     */
    @Override
    public boolean existsByName(String identifier) {
        return accounts.containsKey(identifier);
    }

    /**
     * Update a user's password and persist the change.
     *
     * @param user the user with the updated password
     */
    @Override
    public void changePassword(User user) {
        accounts.put(user.getName(), user);
        save();
    }

    /**
     * Update a user's highscores and persist the change.
     *
     * @param user the user with the updated highscores
     */
    @Override
    public void changeHighscore(String username, String gameName, Integer score) {
        User user = accounts.get(username);
        if (user != null) {
            user.getHighscores().put(gameName, score);
        }
        save();
    }

    @Override
    public Map<String, Integer> getHighscoresByName(String username) {
        User user = accounts.get(username);
        if (user != null) {
            return new HashMap<>(user.getHighscores());
        }
        return new HashMap<>();
    }

    @Override
    public List<User> getTopUsersForGame(String gameName, int limit) {
        List<User> usersWithScores = new ArrayList<>();

        // Filter users who have played this game
        for (User user : accounts.values()) {
            HashMap<String, Integer> highscores = new HashMap<>(user.getHighscores());
            if (highscores.containsKey(gameName)) {
                usersWithScores.add(user);
            }
        }

        // Sort by score in descending order
        usersWithScores.sort((u1, u2) -> {
            Integer score1 = u1.getHighscores().get(gameName);
            Integer score2 = u2.getHighscores().get(gameName);
            return Integer.compare(score2, score1);
        });

        // Return top N users
        return usersWithScores.subList(0, Math.min(limit, usersWithScores.size()));
    }
}
