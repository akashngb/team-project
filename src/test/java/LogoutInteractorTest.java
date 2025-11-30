import use_case.logout.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

public class LogoutInteractorTest {

    private LogoutUserDataAccessInterface mockDAO;
    private LogoutOutputBoundary mockPresenter;
    private LogoutInteractor interactor;

    @BeforeEach
    void setUp() {
        mockDAO = mock(LogoutUserDataAccessInterface.class);
        mockPresenter = mock(LogoutOutputBoundary.class);
        interactor = new LogoutInteractor(mockDAO, mockPresenter);
    }

    @Test
    void testLogoutSuccess() {
        String loggedInUser = "TestUser";

        // Mock DAO to return a username
        when(mockDAO.getCurrentUsername()).thenReturn(loggedInUser);

        interactor.execute();

        // Verify DAO was instructed to clear the current username
        verify(mockDAO, times(1)).setCurrentUsername(null);

        // Verify Presenter was called with the LogoutOutputData (containing the username)
        verify(mockPresenter, times(1)).prepareSuccessView(any(LogoutOutputData.class));
    }
}