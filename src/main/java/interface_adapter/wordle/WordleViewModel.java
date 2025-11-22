package interface_adapter.wordle;

import entity.wordle.Guess;
import entity.wordle.LetterState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Simple view model for the UI layer.
 */
public class WordleViewModel {
    public final List<String> guessWords;
    public final List<List<LetterState>> guessStates;
    public final int attemptsLeft;
    public final boolean finished;
    public final boolean won;
    public final String answerIfFinished;
    public final String message;

    public WordleViewModel(List<Guess> guesses, int attemptsLeft, boolean finished, boolean won, String answerIfFinished, String message) {
        this.guessWords = guesses.stream().map(Guess::getGuess).collect(Collectors.toList());
        this.guessStates = new ArrayList<>();
        for (Guess g : guesses) guessStates.add(g.getStates());
        this.attemptsLeft = attemptsLeft;
        this.finished = finished;
        this.won = won;
        this.answerIfFinished = answerIfFinished;
        this.message = message;
    }
}
