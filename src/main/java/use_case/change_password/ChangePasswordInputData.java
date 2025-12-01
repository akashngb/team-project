package use_case.change_password;

import java.util.Map;

/**
 * The input data for the Change Password Use Case.
 */
public class ChangePasswordInputData {

    private final String password;
    private final String username;
    private final Map<String, Integer> highscores;

    public ChangePasswordInputData(String password, String username, Map<String, Integer> highscores) {
        this.password = password;
        this.username = username;
        this.highscores = highscores;
    }

    String getPassword() {
        return password;
    }

    String getUsername() {
        return username;
    }

    Map<String, Integer> getHighscores() {
        return highscores;
    }
}
