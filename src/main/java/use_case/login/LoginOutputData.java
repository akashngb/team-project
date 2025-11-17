package use_case.login;

/**
 * Output Data for the Login Use Case.
 */
public class LoginOutputData {

    private final String username;
    private final String apiToken; // added init for apiToken

    public LoginOutputData(String username, String apiToken) {
        this.username = username;
        this.apiToken = apiToken; // added apiToken
    }

    public String getUsername() {
        return username;
    }

    public String getApiToken() { // added getApiToken method
        return apiToken;
    }
}
