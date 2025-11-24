package use_case.chess_puzzle;

/**
 * Output data for move checking.
 */
public class CheckMoveOutputData {
    private final String feedback;
    private final boolean isCorrect;
    private final boolean puzzleSolved;
    private final String computerMove;  // Add this field

    public CheckMoveOutputData(String feedback, boolean isCorrect, boolean puzzleSolved, String computerMove) {
        this.feedback = feedback;
        this.isCorrect = isCorrect;
        this.puzzleSolved = puzzleSolved;
        this.computerMove = computerMove;
    }

    public String getFeedback() {
        return feedback;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public boolean isPuzzleSolved() {
        return puzzleSolved;
    }

    public String getComputerMove() {
        return computerMove;
    }
}
