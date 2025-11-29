package interface_adapter.wordle;

import use_case.wordle.*;

public class WordleController {
    private final StartGameInputBoundary startBoundary;
    private final SubmitGuessInputBoundary submitBoundary;
    private final GameSessionGateway gameSessionGateway;


    public WordleController(StartGameInputBoundary startBoundary, SubmitGuessInputBoundary submitBoundary,
                            GameSessionGateway gameSessionGateway) {
        this.startBoundary = startBoundary;
        this.submitBoundary = submitBoundary;
        this.gameSessionGateway = gameSessionGateway;
    }

    public int getScore(String userId) {
        return gameSessionGateway.getScore(userId);
    }

    public void startNewGame(String userId) {
        startBoundary.start(new StartGameInputData(userId));
    }

    public void submitGuess(String userId, String guess) {
        submitBoundary.submitGuess(new SubmitGuessInputData(userId, guess));
    }
}
