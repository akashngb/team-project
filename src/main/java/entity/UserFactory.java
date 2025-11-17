package entity;

/**
 * Factory for creating CommonUser objects.
 */
public class UserFactory {
    public User create(String name, String password, String apiToken) {
        return new User(name, password, apiToken);
    }
}
