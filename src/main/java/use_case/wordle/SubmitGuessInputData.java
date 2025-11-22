package use_case.wordle;

public class SubmitGuessInputData {
    private final String userId;
    private final String guess;

    public SubmitGuessInputData(String userId, String guess) {
        this.userId = userId;
        this.guess = guess;
    }

    public String getUserId() { return userId; }
    public String getGuess() { return guess; }
}
