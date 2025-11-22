package interface_adapter.wordle;

import use_case.wordle.*;

public class WordleController {
    private final StartGameInputBoundary startBoundary;
    private final SubmitGuessInputBoundary submitBoundary;

    public WordleController(StartGameInputBoundary startBoundary, SubmitGuessInputBoundary submitBoundary) {
        this.startBoundary = startBoundary;
        this.submitBoundary = submitBoundary;
    }

    public void startNewGame(String userId) {
        startBoundary.start(new StartGameInputData(userId));
    }

    public void submitGuess(String userId, String guess) {
        submitBoundary.submitGuess(new SubmitGuessInputData(userId, guess));
    }
}
