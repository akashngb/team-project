package app;

import data_access.FileUserDataAccessObject;
import entity.UserFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.ChangePasswordController;
import interface_adapter.logged_in.ChangePasswordPresenter;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.logout.LogoutPresenter;
import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;
import use_case.change_password.ChangePasswordInputBoundary;
import use_case.change_password.ChangePasswordInteractor;
import use_case.change_password.ChangePasswordOutputBoundary;
import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputBoundary;
import use_case.logout.LogoutInputBoundary;
import use_case.logout.LogoutInteractor;
import use_case.logout.LogoutOutputBoundary;
import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInteractor;
import use_case.signup.SignupOutputBoundary;
import use_case.leaderboard.LeaderBoardInputBoundary;
import use_case.leaderboard.LeaderBoardInteractor;
import use_case.leaderboard.LeaderBoardOutputBoundary;
import interface_adapter.leaderboard.LeaderBoardController;
import interface_adapter.leaderboard.LeaderBoardPresenter;
import interface_adapter.leaderboard.LeaderBoardViewModel;
import view.*;
import view.FontLoader;
// Wordle imports
import data_access.wordle.FileWordListDataAccess;
import data_access.wordle.InMemoryGameSessionGateway;
import interface_adapter.wordle.WordleController;
import interface_adapter.wordle.WordlePresenter;
import interface_adapter.wordle.WordleViewModel;
import use_case.wordle.StartGameInteractor;
import use_case.wordle.SubmitGuessInteractor;
import wordle.WordleView;

// Chess Puzzle Imports
import entity.ChessPuzzle;
import data_access.RapidAPIChessPuzzleDataAccess;
import interface_adapter.chess_puzzle.*;
import use_case.chess_puzzle.*;



import javax.swing.*;
import java.awt.*;

public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    final UserFactory userFactory = new UserFactory();
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    // File-based data access implementation using local JSON storage
    final FileUserDataAccessObject userDataAccessObject = new FileUserDataAccessObject("users.json");


    private SignupView signupView;
    private SignupViewModel signupViewModel;
    private LoginViewModel loginViewModel;
    private LoggedInViewModel loggedInViewModel;
    private LoggedInView loggedInView;
    private LoginView loginView;

    //Leaderboard views
    private LeaderBoardView leaderBoardView;
    private LeaderBoardViewModel leaderBoardViewModel;
    private LeaderBoardController leaderBoardController;

    //Wordle views
    private WordleView wordleView;
    private WordleViewModel wordleViewModel;
    private WordleController wordleController;

    //Chess views
    private ChessPuzzleView chessPuzzleView;
    private ChessPuzzleViewModel chessPuzzleViewModel;
    private CheckMoveInteractor checkMoveInteractor;

    public AppBuilder() {
        FontLoader.loadFonts();
        cardPanel.setLayout(cardLayout);
    }

    public AppBuilder addSignupView() {
        signupViewModel = new SignupViewModel();
        signupView = new SignupView(signupViewModel);
        cardPanel.add(signupView, signupView.getViewName());
        return this;
    }

    public AppBuilder addLoginView() {
        loginViewModel = new LoginViewModel();
        loginView = new LoginView(loginViewModel, viewManagerModel);
        cardPanel.add(loginView, loginView.getViewName());
        return this;
    }

    public AppBuilder addLoggedInView() {
        loggedInViewModel = new LoggedInViewModel();
        loggedInView = new LoggedInView(loggedInViewModel, viewManagerModel);
        cardPanel.add(loggedInView, loggedInView.getViewName());
        return this;
    }

    /**
     * Wire up the leaderboard controller to LoggedInView so it can pass it to games
     * Call this AFTER addLeaderBoardUseCase()
     */
    public AppBuilder wireLeaderBoardToLoggedInView() {
        if (loggedInView != null && leaderBoardController != null) {
            loggedInView.setLeaderBoardController(leaderBoardController);
        }
        return this;
    }

    public AppBuilder addSignupUseCase() {
        final SignupOutputBoundary signupOutputBoundary = new SignupPresenter(viewManagerModel,
                signupViewModel, loginViewModel);
        final SignupInputBoundary userSignupInteractor = new SignupInteractor(
                userDataAccessObject, signupOutputBoundary, userFactory);

        SignupController controller = new SignupController(userSignupInteractor);
        signupView.setSignupController(controller);
        return this;
    }

    public AppBuilder addLoginUseCase() {
        final LoginOutputBoundary loginOutputBoundary = new LoginPresenter(viewManagerModel,
                loggedInViewModel, loginViewModel);
        final LoginInputBoundary loginInteractor = new LoginInteractor(
                userDataAccessObject, loginOutputBoundary);

        LoginController loginController = new LoginController(loginInteractor);
        loginView.setLoginController(loginController);
        return this;
    }

    /**
     * Adds the Logout Use Case to the application.
     * @return this builder
     */
    public AppBuilder addLogoutUseCase() {
        final LogoutOutputBoundary logoutOutputBoundary = new LogoutPresenter(viewManagerModel,
                loggedInViewModel, loginViewModel);

        final LogoutInputBoundary logoutInteractor =
                new LogoutInteractor(userDataAccessObject, logoutOutputBoundary);

        final LogoutController logoutController = new LogoutController(logoutInteractor);
        loggedInView.setLogoutController(logoutController);
        return this;
    }

    public AppBuilder addLeaderBoardView() {
        leaderBoardViewModel = new LeaderBoardViewModel();
        leaderBoardView = new LeaderBoardView(leaderBoardViewModel, viewManagerModel);
        cardPanel.add(leaderBoardView, leaderBoardView.getViewName());
        return this;
    }

    public AppBuilder addLeaderBoardUseCase() {
        final LeaderBoardOutputBoundary leaderBoardOutputBoundary = new LeaderBoardPresenter(
                viewManagerModel, leaderBoardViewModel);
        final LeaderBoardInputBoundary leaderBoardInteractor = new LeaderBoardInteractor(
                userDataAccessObject, leaderBoardOutputBoundary);

        leaderBoardController = new LeaderBoardController(leaderBoardInteractor);
        leaderBoardView.setLeaderBoardController(leaderBoardController);
        return this;
    }

    public AppBuilder addWordleFeature() {
        // Data access
        FileWordListDataAccess wordListDao = new FileWordListDataAccess();
        InMemoryGameSessionGateway sessionGateway = new InMemoryGameSessionGateway();

        // ViewModel
        wordleViewModel = new WordleViewModel(java.util.Collections.emptyList(), 6, false, false, null, "");

        // Presenter
        WordlePresenter wordlePresenter = new WordlePresenter(vm -> {
            if (wordleView != null) wordleView.setViewModel(vm);
        });

        // Interactors
        StartGameInteractor startGameInteractor = new StartGameInteractor(wordListDao, sessionGateway, wordlePresenter);
        SubmitGuessInteractor submitGuessInteractor = new SubmitGuessInteractor(wordListDao, sessionGateway, wordlePresenter);

        // Controller
        wordleController = new WordleController(startGameInteractor, submitGuessInteractor, sessionGateway);

        wordleView = new WordleView(wordleController, viewManagerModel, vm -> {
            if (wordleView != null) wordleView.setViewModel(vm);
        });

        // Wire up leaderboard controller if available
        if (leaderBoardController != null) {
            wordleView.setLeaderBoardController(leaderBoardController);
        }

        // Setup username tracking from logged in state
        loggedInViewModel.addPropertyChangeListener(evt -> {
            if ("state".equals(evt.getPropertyName())) {
                String currentUser = loggedInViewModel.getState().getUsername();
                if (currentUser != null && !currentUser.isEmpty()) {
                    wordleView.setUserId(currentUser);
                }
            }
        });

        cardPanel.add(wordleView, "WORDLE");


        return this;
    }

    public AppBuilder addChessPuzzleView() {
        chessPuzzleViewModel = new ChessPuzzleViewModel();
        chessPuzzleView = new ChessPuzzleView(chessPuzzleViewModel, viewManagerModel);

        JPanel wrapper = new JPanel(new GridBagLayout()); // centers contents
        wrapper.add(chessPuzzleView);

        cardPanel.add(wrapper, chessPuzzleView.getViewName());
        return this;
    }


    public AppBuilder addChessPuzzleUseCase() {
        // Setup data access
        final ChessPuzzleDataAccessInterface dataAccess = new RapidAPIChessPuzzleDataAccess();

        // Setup Load Puzzles use case
        final LoadPuzzlesOutputBoundary loadPresenter = new LoadPuzzlesPresenter(chessPuzzleViewModel);
        final LoadPuzzlesInputBoundary loadInteractor = new LoadPuzzlesInteractor(dataAccess, loadPresenter);
        final LoadPuzzlesController loadController = new LoadPuzzlesController(loadInteractor);

        // Setup Check Move use case
        final CheckMoveOutputBoundary checkPresenter = new CheckMovePresenter(chessPuzzleViewModel);
        checkMoveInteractor = new CheckMoveInteractor(checkPresenter);
        final CheckMoveController checkController = new CheckMoveController(checkMoveInteractor);

        // Connect controllers to view
        chessPuzzleView.setLoadPuzzlesController(loadController);
        chessPuzzleView.setCheckMoveController(checkController);

        // Wire up leaderboard controller
        if (leaderBoardController != null) {
            chessPuzzleView.setLeaderBoardController(leaderBoardController);
        }

        // Setup username tracking from logged in state
        loggedInViewModel.addPropertyChangeListener(evt -> {
            if ("state".equals(evt.getPropertyName())) {
                String currentUser = loggedInViewModel.getState().getUsername();
                if (currentUser != null && !currentUser.isEmpty()) {
                    chessPuzzleView.setUserId(currentUser);
                }
            }
        });

        // Add property change listener to set current puzzle
        chessPuzzleViewModel.addPropertyChangeListener(evt -> {
            ChessPuzzleState state = chessPuzzleViewModel.getState();
            ChessPuzzle currentPuzzle = state.getCurrentPuzzle();
            if (currentPuzzle != null) {
                checkMoveInteractor.setCurrentPuzzle(currentPuzzle);
            }
        });

        return this;
    }

    public JFrame build() {
        final JFrame application = new JFrame("User Login Example");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        application.setMinimumSize(new Dimension(1300, 700));
        application.setResizable(false);

        application.add(cardPanel);

        viewManagerModel.setState(loginView.getViewName());
        viewManagerModel.firePropertyChange();

        return application;
    }
}
