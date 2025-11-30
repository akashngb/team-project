import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import use_case.signup.*;
import entity.User;
import entity.UserFactory;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class SignupInteractorTest {

    private SignupUserDataAccessInterface mockDAO;
    private SignupOutputBoundary mockPresenter;
    private UserFactory mockFactory;
    private SignupInteractor interactor;

    @BeforeEach
    void setUp() {
        // Initialize mocks before each test
        mockDAO = mock(SignupUserDataAccessInterface.class);
        mockPresenter = mock(SignupOutputBoundary.class);
        mockFactory = mock(UserFactory.class);
        
        // Create the interactor instance being tested
        interactor = new SignupInteractor(mockDAO, mockPresenter, mockFactory);
    }

    // Test Success Path
    @Test
    void testSignupSuccess() {
        // Arrange
        String username = "TestUser";
        String password = "TestPassword123";
        SignupInputData inputData = new SignupInputData(username, password, password);
        
        // Mock: DAO says user DOES NOT exist
        when(mockDAO.existsByName(username)).thenReturn(false);
        
        // Mock: Factory returns a new User object
        User createdUser = mock(User.class);
        when(createdUser.getName()).thenReturn(username);
        when(mockFactory.create(username, password)).thenReturn(createdUser);

        interactor.execute(inputData);

        verify(mockDAO, times(1)).save(createdUser);
        
        verify(mockPresenter, times(1)).prepareSuccessView(any(SignupOutputData.class));
        verify(mockPresenter, never()).prepareFailView(anyString());
    }

    // --- 2. Test Failure: User Already Exists ---
    @Test
    void testSignupFailureUserExists() {
        // Arrange
        String username = "ExistingUser";
        String password = "password";
        SignupInputData inputData = new SignupInputData(username, password, password);

        // Mock: DAO says user DOES exist
        when(mockDAO.existsByName(username)).thenReturn(true);

        interactor.execute(inputData);

        verify(mockPresenter, times(1)).prepareFailView("User already exists.");
        
        verify(mockDAO, never()).save(any(User.class));
        verify(mockFactory, never()).create(anyString(), anyString());
        verify(mockPresenter, never()).prepareSuccessView(any(SignupOutputData.class));
    }

    // Test Failure: Passwords Do Not Match
    @Test
    void testSignupFailurePasswordsMismatch() {
        String username = "NewUser";
        SignupInputData inputData = new SignupInputData(username, "pass1", "pass2");

        when(mockDAO.existsByName(username)).thenReturn(false);

        interactor.execute(inputData);

        verify(mockPresenter, times(1)).prepareFailView("Passwords don't match.");
        
        verify(mockDAO, never()).save(any(User.class));
        verify(mockFactory, never()).create(anyString(), anyString());
        verify(mockPresenter, never()).prepareSuccessView(any(SignupOutputData.class));
    }

    // Test Failure: Empty Password
    @Test
    void testSignupFailureEmptyPassword() {

        String username = "NewUser";
        SignupInputData inputData = new SignupInputData(username, "", "");

        when(mockDAO.existsByName(username)).thenReturn(false);

        interactor.execute(inputData);

        verify(mockPresenter, times(1)).prepareFailView("New password cannot be empty");

        verify(mockDAO, never()).save(any(User.class));
        verify(mockFactory, never()).create(anyString(), anyString());
    }

    // Test Failure: Empty Username
    @Test
    void testSignupFailureEmptyUsername() {
        String password = "TestPassword123";
        // the InputData constructor allows us to test the condition.
        SignupInputData inputData = new SignupInputData("", password, password);

        // Mock: DAO says user DOES NOT exist (must pass first two checks)
        when(mockDAO.existsByName("")).thenReturn(false);

        interactor.execute(inputData);

        verify(mockPresenter, times(1)).prepareFailView("Username cannot be empty");

        verify(mockDAO, never()).save(any(User.class));
        verify(mockFactory, never()).create(anyString(), anyString());
    }

    // Test switching to LoginView
    @Test
    void testSwitchToLoginView() {
        interactor.switchToLoginView();

        // Verify the Presenter's corresponding method was called
        verify(mockPresenter, times(1)).switchToLoginView();
    }
}