import use_case.logout.*;
import data_access.FileUserDataAccessObject;
import entity.UserFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class LogoutInteractorTest {

    private FileUserDataAccessObject fileDAO; // Use the real DAO
    private LogoutOutputBoundary mockPresenter;
    private LogoutInteractor interactor;
    private UserFactory userFactory;

    private final String TEST_FILE_PATH = "test_logout_users.csv";
    private final String LOGGED_IN_USER = "LoggedInUser";

    @BeforeEach
    void setUp() throws IOException {
        // Initialize dependencies
        userFactory = new UserFactory();
        mockPresenter = mock(LogoutOutputBoundary.class);
        
        // Instantiate the actual DAO
        // The fileDAO implements LogoutUserDataAccessInterface, so it works here.
        fileDAO = new FileUserDataAccessObject(TEST_FILE_PATH, userFactory);
        
        // Set up known state: Simulate a user being logged in
        fileDAO.setCurrentUsername(LOGGED_IN_USER); 
        
        // Create Interactor, injecting the actual DAO
        interactor = new LogoutInteractor(fileDAO, mockPresenter);
    }

    @AfterEach
    void tearDown() {
        // Delete the physical test file after each run
        // This is necessary if you were using save functionality, but good practice anyway.
        File file = new File(TEST_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
        // Also ensure the currentUsername state is clear on the DAO for subsequent tests
        fileDAO.setCurrentUsername(null);
    }

    @Test
    void testLogoutSuccess() {
        // The DAO is already set up in @BeforeEach to have LOGGED_IN_USER as current user.

        interactor.execute();

        // Assert the side effect: the actual DAO's state was updated
        // The core functionality of Logout is to set the current username to null.
        assertNull(fileDAO.getCurrentUsername(), "The current username in the DAO should be null after logout.");

        // Verify Presenter was called with the LogoutOutputData (containing the username that logged out)
        // We can capture the output data to verify the username passed to the presenter.
        verify(mockPresenter, times(1)).prepareSuccessView(any(LogoutOutputData.class));
    }
}