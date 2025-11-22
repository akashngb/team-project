package use_case.wordle;

public interface WordleOutputBoundary {
    void presentStart(WordleOutputData data);
    void presentGuessResult(WordleOutputData data);
    void presentError(String message);
}
