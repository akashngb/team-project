import use_case.login.*;
import data_access.FileUserDataAccessObject;
import entity.User;
import entity.UserFactory; // Use the provided class
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class LoginInteractorTest {

    private FileUserDataAccessObject fileDAO;
    private LoginOutputBoundary mockPresenter;
    private LoginInteractor interactor;
    private UserFactory userFactory;

    private final String TEST_FILE_PATH = "users.csv";
    private final String VALID_USERNAME = "ActualDAOUser";
    private final String VALID_PASSWORD = "TestPassword999";

    @BeforeEach
    void setUp() throws IOException {
        // Initialize dependencies
        userFactory = new UserFactory(); // Instantiate the provided UserFactory class
        mockPresenter = mock(LoginOutputBoundary.class);
        
        // Instantiate the actual DAO that reads/writes the file
        fileDAO = new FileUserDataAccessObject(TEST_FILE_PATH, userFactory);
        
        // Set up known state: Insert the test user into the file using the real DAO's save logic
        User userToSave = userFactory.create(VALID_USERNAME, VALID_PASSWORD);
        if (!fileDAO.existsByName(VALID_USERNAME)) {
            fileDAO.save(userToSave); 
        }
        
        // Create Interactor, injecting the actual DAO
        interactor = new LoginInteractor(fileDAO, mockPresenter);
    }

    @AfterEach
    void tearDown() {
        // Delete the physical test file after each run
        File file = new File(TEST_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
        // Also ensure the currentUsername state is clear on the DAO
        fileDAO.setCurrentUsername(null);
    }

    // Test Success Path ---
    @Test
    void testLoginSuccess() {
        LoginInputData inputData = new LoginInputData(VALID_USERNAME, VALID_PASSWORD);

        interactor.execute(inputData);

        // Verify success view preparation
        verify(mockPresenter, times(1)).prepareSuccessView(any(LoginOutputData.class));
        verify(mockPresenter, never()).prepareFailView(anyString());
        
        // Assert the side effect: the actual DAO's state was updated
        assertEquals(VALID_USERNAME, fileDAO.getCurrentUsername());
    }

    // Test Failure: User Does Not Exist ---
    @Test
    void testLoginFailureUserNotFound() {
        String nonExistentUser = "GhostUser" + System.currentTimeMillis();
        LoginInputData inputData = new LoginInputData(nonExistentUser, VALID_PASSWORD);

        interactor.execute(inputData);

        // Verify failure view preparation
        verify(mockPresenter, times(1)).prepareFailView(nonExistentUser + ": Account does not exist.");
        
        // Assert no side effect occurred
        assertNull(fileDAO.getCurrentUsername());
    }

    // Test Failure: Incorrect Password ---
    @Test
    void testLoginFailureIncorrectPassword() {
        LoginInputData inputData = new LoginInputData(VALID_USERNAME, "WrongPassword");

        interactor.execute(inputData);

        // Verify failure view preparation
        verify(mockPresenter, times(1)).prepareFailView("Incorrect password for \"" + VALID_USERNAME + "\".");
        
        // Verify success view was not called and no side effect occurred
        verify(mockPresenter, never()).prepareSuccessView(any(LoginOutputData.class));
        assertNull(fileDAO.getCurrentUsername());
    }
}
