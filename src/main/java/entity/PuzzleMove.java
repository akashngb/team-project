package entity;

/**
 * Represents a chess move from one square to another.
 */
public class PuzzleMove {
    private final String from;  // e.g., "e2"
    private final String to;    // e.g., "e4"
    private final String promotion; // e.g., "q" for queen, null if no promotion

    public PuzzleMove(String from, String to, String promotion) {
        this.from = from;
        this.to = to;
        this.promotion = promotion;
    }

    public PuzzleMove(String from, String to) {
        this(from, to, null);
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getPromotion() {
        return promotion;
    }

    public String toAlgebraic() {
        if (promotion != null) {
            return from + to + promotion;
        }
        return from + to;
    }
}
