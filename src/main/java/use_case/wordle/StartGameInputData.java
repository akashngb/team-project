package use_case.wordle;

public class StartGameInputData {
    private final String userId;
    private final boolean deterministic;
    private final int answerIndex;

    public StartGameInputData(String userId) {
        this(userId, false, -1);
    }

    public StartGameInputData(String userId, boolean deterministic, int answerIndex) {
        this.userId = userId;
        this.deterministic = deterministic;
        this.answerIndex = answerIndex;
    }

    public String getUserId() { return userId; }
    public boolean isDeterministic() { return deterministic; }
    public int getAnswerIndex() { return answerIndex; }
}
