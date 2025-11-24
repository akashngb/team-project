package entity.wordle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WordleGame {
    public static final int WORD_LENGTH = 5;
    public static final int MAX_ATTEMPTS = 6;

    private final String answer;
    private final List<Guess> guesses = new ArrayList<>();
    private boolean finished = false;
    private boolean won = false;

    public WordleGame(String answer) {
        Objects.requireNonNull(answer);
        if (answer.length() != WORD_LENGTH) throw new IllegalArgumentException("Answer must be length " + WORD_LENGTH);
        this.answer = answer.toLowerCase();
    }

    public synchronized Guess submitGuess(String guessRaw) {
        if (finished) throw new IllegalStateException("Game finished");
        String guess = Objects.requireNonNull(guessRaw).toLowerCase();
        if (guess.length() != WORD_LENGTH) throw new IllegalArgumentException("Guess must be length " + WORD_LENGTH);

        List<LetterState> states = evaluate(guess, answer);
        Guess g = new Guess(guess, states);
        guesses.add(g);

        if (isWinning(states)) {
            finished = true;
            won = true;
        } else if (guesses.size() >= MAX_ATTEMPTS) {
            finished = true;
            won = false;
        }
        return g;
    }

    public List<Guess> getGuesses() { return List.copyOf(guesses); }
    public boolean isFinished() { return finished; }
    public boolean isWon() { return won; }
    public String getAnswer() { return answer; }

    private static boolean isWinning(List<LetterState> states) {
        for (LetterState s : states) if (s != LetterState.CORRECT) return false;
        return true;
    }

    /**
     * Evaluation algorithm that correctly handles duplicate letters.
     * Returns a list of LetterState for each position in guess.
     */
    public static List<LetterState> evaluate(String guess, String answer) {
        if (guess.length() != WORD_LENGTH || answer.length() != WORD_LENGTH)
            throw new IllegalArgumentException("Both guess and answer must be length " + WORD_LENGTH);

        LetterState[] states = new LetterState[WORD_LENGTH];
        char[] g = guess.toLowerCase().toCharArray();
        char[] a = answer.toLowerCase().toCharArray();

        Map<Character, Integer> remaining = new HashMap<>();
        // First pass: mark CORRECT and build remaining counts for non-matching answer letters
        for (int i = 0; i < WORD_LENGTH; i++) {
            if (g[i] == a[i]) {
                states[i] = LetterState.CORRECT;
            } else {
                remaining.put(a[i], remaining.getOrDefault(a[i], 0) + 1);
            }
        }
        // Second pass: mark PRESENT or ABSENT
        for (int i = 0; i < WORD_LENGTH; i++) {
            if (states[i] == LetterState.CORRECT) continue;
            char gc = g[i];
            Integer count = remaining.get(gc);
            if (count != null && count > 0) {
                states[i] = LetterState.PRESENT;
                remaining.put(gc, count - 1);
            } else {
                states[i] = LetterState.ABSENT;
            }
        }
        List<LetterState> out = new ArrayList<>(WORD_LENGTH);
        for (LetterState s : states) out.add(s);
        return out;
    }
}
