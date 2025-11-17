package use_case.change_password;

/**
 * The input data for the Change Password Use Case.
 */
public class ChangePasswordInputData {

    private final String password;
    private final String username;
    private final String apiToken;

    public ChangePasswordInputData(String password, String username, String apiToken) {
        this.password = password;
        this.username = username;
        this.apiToken = apiToken;
    }

    String getPassword() {
        return password;
    }

    String getUsername() {
        return username;
    }

    String getApiToken() { return apiToken; }

}
