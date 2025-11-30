import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.login.*;
import data_access.MongoDBUserDataAccessObject;
import entity.User;
import static org.mockito.Mockito.*;

public class MongoDBLoginInteractorTest {

    // Mock the concrete DAO since it has the verifyPassword method
    private MongoDBUserDataAccessObject mockDAO;
    private LoginOutputBoundary mockPresenter;
    private MongoDBLoginInteractor interactor;

    @BeforeEach
    void setUp() {
        mockDAO = mock(MongoDBUserDataAccessObject.class);
        mockPresenter = mock(LoginOutputBoundary.class);
        interactor = new MongoDBLoginInteractor(mockDAO, mockPresenter);
    }

    // Test Success Path: User exists and password verifies via BCrypt
    @Test
    void testLoginSuccess() {
        String username = "MongoUser";
        String password = "CorrectPassword123";
        LoginInputData inputData = new LoginInputData(username, password);

        // Mock DAO behavior for success
        when(mockDAO.existsByName(username)).thenReturn(true);
        when(mockDAO.verifyPassword(username, password)).thenReturn(true);
        User mockUser = mock(User.class);
        when(mockUser.getName()).thenReturn(username);
        when(mockDAO.get(username)).thenReturn(mockUser);

        interactor.execute(inputData);

        // Verify success view preparation and side effect
        verify(mockPresenter, times(1)).prepareSuccessView(any(LoginOutputData.class));
        verify(mockDAO, times(1)).setCurrentUsername(username);
        verify(mockPresenter, never()).prepareFailView(anyString());
    }

    // Test Failure: User Does Not Exist (First Check)
    @Test
    void testLoginFailureUserNotFound() {
        String username = "NonExistentUser";
        String password = "AnyPassword";
        LoginInputData inputData = new LoginInputData(username, password);

        // Mock DAO behavior for failure (user not found)
        when(mockDAO.existsByName(username)).thenReturn(false);

        interactor.execute(inputData);

        // Verify failure view preparation with correct message
        verify(mockPresenter, times(1)).prepareFailView(username + ": Account does not exist.");
        
        // Verify BCrypt password check was never called
        verify(mockDAO, never()).verifyPassword(anyString(), anyString());
    }

    // Test Failure: Incorrect Password (BCrypt Check Fails)
    @Test
    void testLoginFailureIncorrectPassword() {
        String username = "ExistingUser";
        String wrongPassword = "WrongPassword";
        LoginInputData inputData = new LoginInputData(username, wrongPassword);

        // Mock DAO behavior for failure (user exists but password fails verification)
        when(mockDAO.existsByName(username)).thenReturn(true);
        when(mockDAO.verifyPassword(username, wrongPassword)).thenReturn(false);

        interactor.execute(inputData);

        // Verify failure view preparation with correct message
        verify(mockPresenter, times(1)).prepareFailView("Incorrect password for \"" + username + "\".");
        
        // Verify side effects were never called
        verify(mockDAO, never()).setCurrentUsername(anyString());
        verify(mockPresenter, never()).prepareSuccessView(any(LoginOutputData.class));
    }
}