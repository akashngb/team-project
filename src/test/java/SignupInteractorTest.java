import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.signup.*;
import entity.User;
import entity.UserFactory;
import data_access.FileUserDataAccessObject;
import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class SignupInteractorTest {

    // Replace Mocks with real implementations
    private FileUserDataAccessObject fileDAO; 
    private SignupOutputBoundary mockPresenter;
    private UserFactory userFactory;
    private SignupInteractor interactor;

    private final String TEST_FILE_PATH = "users.csv";
    private final String VALID_USERNAME = "NewUserToSignup";
    private final String VALID_PASSWORD = "TestPassword123";

    @BeforeEach
    void setUp() throws IOException {
        // Initialize dependencies
        userFactory = new UserFactory(); // Use the real factory
        mockPresenter = mock(SignupOutputBoundary.class); // Presenter remains mocked
        
        // Instantiate the actual DAO
        fileDAO = new FileUserDataAccessObject(TEST_FILE_PATH, userFactory);
        
        // Create Interactor, injecting the real DAO and Factory
        // The DAO implements SignupUserDataAccessInterface, so it works here.
        interactor = new SignupInteractor(fileDAO, mockPresenter, userFactory);
    }
    
    @AfterEach
    void tearDown() {
        // Delete the physical test file after each run
        File file = new File(TEST_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    // Test Success Path
    @Test
    void testSignupSuccess() {
        // Arrange
        SignupInputData inputData = new SignupInputData(VALID_USERNAME, VALID_PASSWORD, VALID_PASSWORD);
        
        // Ensure user does not exist before test (cleanup in @AfterEach helps this)
        assertFalse(fileDAO.existsByName(VALID_USERNAME));

        interactor.execute(inputData);

        // Verify side effect: Check if the user was saved to the file
        assertTrue(fileDAO.existsByName(VALID_USERNAME), "User should be saved to the DAO.");
        
        // Verify success view preparation
        verify(mockPresenter, times(1)).prepareSuccessView(any(SignupOutputData.class));
        verify(mockPresenter, never()).prepareFailView(anyString());
    }

    // Test Failure: User Already Exists
    @Test
    void testSignupFailureUserExists() {
        // Arrange
        String existingUser = "ExistingUser1";
        SignupInputData inputData = new SignupInputData(existingUser, VALID_PASSWORD, VALID_PASSWORD);
        
        // Pre-condition: Save the user using the real DAO to simulate existence
        User userToSave = userFactory.create(existingUser, VALID_PASSWORD);
        fileDAO.save(userToSave);
        assertTrue(fileDAO.existsByName(existingUser)); // Sanity check

        interactor.execute(inputData);

        // Verify failure view preparation
        verify(mockPresenter, times(1)).prepareFailView("User already exists.");
        
        // Verify no new save operation occurred
        // Since we are using the real DAO, we must rely on the presenter verification
        verify(mockPresenter, never()).prepareSuccessView(any(SignupOutputData.class));
    }

    // Test Failure: Passwords Do Not Match
    @Test
    void testSignupFailurePasswordsMismatch() {
        // Arrange: DAO check is not needed as failure occurs earlier
        SignupInputData inputData = new SignupInputData(VALID_USERNAME, "pass1", "pass2");

        interactor.execute(inputData);

        // Verify failure view preparation
        verify(mockPresenter, times(1)).prepareFailView("Passwords don't match.");
        
        // Verify no success call
        verify(mockPresenter, never()).prepareSuccessView(any(SignupOutputData.class));
    }

    // Test Failure: Empty Password
    @Test
    void testSignupFailureEmptyPassword() {
        // Arrange: DAO check is not needed
        SignupInputData inputData = new SignupInputData(VALID_USERNAME, "", "");

        interactor.execute(inputData);

        // Verify failure view preparation
        verify(mockPresenter, times(1)).prepareFailView("New password cannot be empty");
        
        // Verify no success call
        verify(mockPresenter, never()).prepareSuccessView(any(SignupOutputData.class));
    }

    // Test Failure: Empty Username
    @Test
    void testSignupFailureEmptyUsername() {
        // Arrange: DAO check is not needed
        SignupInputData inputData = new SignupInputData("", VALID_PASSWORD, VALID_PASSWORD);

        interactor.execute(inputData);

        // Verify failure view preparation
        verify(mockPresenter, times(1)).prepareFailView("Username cannot be empty");
        
        // Verify no success call
        verify(mockPresenter, never()).prepareSuccessView(any(SignupOutputData.class));
    }

    // Test switching to LoginView
    @Test
    void testSwitchToLoginView() {
        interactor.switchToLoginView();

        // Verify the Presenter's corresponding method was called
        verify(mockPresenter, times(1)).switchToLoginView();
    }
}
