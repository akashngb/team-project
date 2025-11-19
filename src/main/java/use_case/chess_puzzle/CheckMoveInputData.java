package use_case.chess_puzzle;

/**
 * Input data for checking a move.
 */
public class CheckMoveInputData {
    private final String move;
    private final int moveIndex;

    public CheckMoveInputData(String move, int moveIndex) {
        this.move = move;
        this.moveIndex = moveIndex;
    }

    public String getMove() {
        return move;
    }

    public int getMoveIndex() {
        return moveIndex;
    }
}