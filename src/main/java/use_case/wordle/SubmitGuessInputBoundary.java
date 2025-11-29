package use_case.wordle;

public interface SubmitGuessInputBoundary {
    void submitGuess(SubmitGuessInputData request);
    int getScore(String userId);
}
