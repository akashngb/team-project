package use_case.login;

import data_access.MongoDBUserDataAccessObject;
import entity.User;

/**
 * MongoDB-specific Login Interactor that properly handles BCrypt password verification.
 * Use this instead of the regular LoginInteractor when using MongoDB.
 */
public class MongoDBLoginInteractor implements LoginInputBoundary {
    private final MongoDBUserDataAccessObject userDataAccessObject;
    private final LoginOutputBoundary loginPresenter;

    public MongoDBLoginInteractor(MongoDBUserDataAccessObject userDataAccessInterface,
                                   LoginOutputBoundary loginOutputBoundary) {
        this.userDataAccessObject = userDataAccessInterface;
        this.loginPresenter = loginOutputBoundary;
    }

    @Override
    public void execute(LoginInputData loginInputData) {
        final String username = loginInputData.getUsername();
        final String password = loginInputData.getPassword();

        if (!userDataAccessObject.existsByName(username)) {
            loginPresenter.prepareFailView(username + ": Account does not exist.");
        }
        else {
            // Use BCrypt to verify the password instead of direct comparison
            if (!userDataAccessObject.verifyPassword(username, password)) {
                loginPresenter.prepareFailView("Incorrect password for \"" + username + "\".");
            }
            else {
                final User user = userDataAccessObject.get(username);
                userDataAccessObject.setCurrentUsername(username);

                final LoginOutputData loginOutputData = new LoginOutputData(user.getName());
                loginPresenter.prepareSuccessView(loginOutputData);
            }
        }
    }
}

