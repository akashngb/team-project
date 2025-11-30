import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.login.*;
import entity.User;
import static org.mockito.Mockito.*;

public class LoginInteractorTest {

    private LoginUserDataAccessInterface mockDAO;
    private LoginOutputBoundary mockPresenter;
    private LoginInteractor interactor;

    @BeforeEach
    void setUp() {
        mockDAO = mock(LoginUserDataAccessInterface.class);
        mockPresenter = mock(LoginOutputBoundary.class);
        interactor = new LoginInteractor(mockDAO, mockPresenter);
    }

    // Test Success Path
    @Test
    void testLoginSuccess() {
        String username = "ValidUser";
        String password = "CorrectPassword123";
        LoginInputData inputData = new LoginInputData(username, password);

        // Mock DAO behavior for success
        when(mockDAO.existsByName(username)).thenReturn(true);
        User mockUser = mock(User.class);
        when(mockUser.getName()).thenReturn(username);
        when(mockUser.getPassword()).thenReturn(password);
        when(mockDAO.get(username)).thenReturn(mockUser);

        interactor.execute(inputData);

        // Verify success view preparation
        verify(mockPresenter, times(1)).prepareSuccessView(any(LoginOutputData.class));
        verify(mockPresenter, never()).prepareFailView(anyString());
        
        // Verify DAO method was called to set current user
        verify(mockDAO, times(1)).setCurrentUsername(username);
    }

    // Test Failure: User Does Not Exist
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
        verify(mockPresenter, never()).prepareSuccessView(any(LoginOutputData.class));
        
        // Verify DAO methods for getting user/setting current user were never called
        verify(mockDAO, never()).get(anyString());
        verify(mockDAO, never()).setCurrentUsername(anyString());
    }

    // Test Failure: Incorrect Password
    @Test
    void testLoginFailureIncorrectPassword() {
        String username = "ExistingUser";
        String correctPassword = "CorrectPassword123";
        String wrongPassword = "WrongPassword";
        LoginInputData inputData = new LoginInputData(username, wrongPassword);

        // Mock DAO behavior for failure (incorrect password)
        when(mockDAO.existsByName(username)).thenReturn(true);
        User mockUser = mock(User.class);
        when(mockUser.getPassword()).thenReturn(correctPassword);
        when(mockDAO.get(username)).thenReturn(mockUser);

        interactor.execute(inputData);

        // Verify failure view preparation with correct message
        verify(mockPresenter, times(1)).prepareFailView("Incorrect password for \"" + username + "\".");
        verify(mockPresenter, never()).prepareSuccessView(any(LoginOutputData.class));
        
        // Verify DAO method to set current user was never called
        verify(mockDAO, never()).setCurrentUsername(anyString());
    }
}