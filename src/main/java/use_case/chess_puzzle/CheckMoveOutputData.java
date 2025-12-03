package use_case.chess_puzzle;

/**
 * Output data for move checking.
 */
public class CheckMoveOutputData {
    private final String feedback; // For analysis
    public final boolean isCorrect; // Check correctness
    private final boolean puzzleSolved; // Check wheter the puzzle is solved
    private final String computerMove;  // Added this field for opponent move

    public CheckMoveOutputData(String feedback, boolean isCorrect, boolean puzzleSolved, String computerMove) {
        this.feedback = feedback;
        this.isCorrect = isCorrect;
        this.puzzleSolved = puzzleSolved;
        this.computerMove = computerMove;
    }

    public String getFeedback() {
        return feedback;
    }

    public String getComputerMove() {
        return computerMove;
    }

    public String getMessage() {
        return feedback;
    }

    public boolean isPuzzleSolved() {
        return puzzleSolved;
    }

    public boolean isCorrect() {
        return isCorrect;
    }
}
