package interface_adapter.logged_in;

import use_case.change_password.ChangePasswordInputBoundary;
import use_case.change_password.ChangePasswordInputData;

import java.util.Map;

/**
 * Controller for the Change Password Use Case.
 */
public class ChangePasswordController {
    private final ChangePasswordInputBoundary userChangePasswordUseCaseInteractor;

    public ChangePasswordController(ChangePasswordInputBoundary userChangePasswordUseCaseInteractor) {
        this.userChangePasswordUseCaseInteractor = userChangePasswordUseCaseInteractor;
    }

    /**
     * Executes the Change Password Use Case.
     * @param password the new password
     * @param username the user whose password to change
     * @param highscores the user's highscores
     */
    public void execute(String password, String username, Map<String, Integer> highscores) {
        final ChangePasswordInputData changePasswordInputData = new ChangePasswordInputData(username, password, highscores);

        userChangePasswordUseCaseInteractor.execute(changePasswordInputData);
    }
}
