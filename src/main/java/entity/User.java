package entity;

/**
 * A simple entity representing a user. Users have a username, password and API token.
 */
public class User {

    private final String name;
    private final String password;
    private final String apiToken;

    /**
     * Creates a new user with the given params.
     * @param name the username
     * @param password the password
     * @param apiToken the API token
     * @throws IllegalArgumentException if any params are empty
     */
    public User(String name, String password, String apiToken) {
        if ("".equals(name)) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if ("".equals(password)) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if ("".equals(apiToken)) {
            throw new IllegalArgumentException("API token cannot be empty");
        }
        this.name = name;
        this.password = password;
        this.apiToken = apiToken;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getApiToken() { return apiToken; }
}
