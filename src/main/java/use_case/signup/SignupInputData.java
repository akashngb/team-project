package use_case.signup;

/**
 * The Input Data for the Signup Use Case.
 */
public class SignupInputData {

    private final String username;
    private final String password;
    private final String repeatPassword;
    private final String apiToken;

    public SignupInputData(String username, String password, String repeatPassword, String apiToken) {
        this.username = username;
        this.password = password;
        this.repeatPassword = repeatPassword;
        this.apiToken = apiToken;
    }

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }

    String getRepeatPassword() { return repeatPassword; }

    String getApiToken() { return apiToken; }
}
