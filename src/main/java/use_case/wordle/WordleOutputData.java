package use_case.wordle;

import entity.wordle.Guess;
import java.util.List;

public class WordleOutputData {
    public final List<Guess> guesses;
    public final int attemptsLeft;
    public final boolean finished;
    public final boolean won;
    public final String answerIfFinished;
    public final String message;

    public WordleOutputData(List<Guess> guesses, int attemptsLeft, boolean finished, boolean won, String answerIfFinished, String message) {
        this.guesses = guesses;
        this.attemptsLeft = attemptsLeft;
        this.finished = finished;
        this.won = won;
        this.answerIfFinished = answerIfFinished;
        this.message = message;
    }
}
