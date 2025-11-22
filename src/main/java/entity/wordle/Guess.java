package entity.wordle;

import java.util.List;
import java.util.Objects;

public final class Guess {
    private final String guess; // normalized lower-case
    private final List<LetterState> states;

    public Guess(String guess, List<LetterState> states) {
        this.guess = Objects.requireNonNull(guess).toLowerCase();
        this.states = List.copyOf(states);
    }

    public String getGuess() { return guess; }
    public List<LetterState> getStates() { return states; }
}
