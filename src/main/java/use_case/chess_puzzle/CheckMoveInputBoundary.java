package use_case.chess_puzzle;

/**
 * Input boundary for checking if a move is correct.
 */
public interface CheckMoveInputBoundary {
    void execute(CheckMoveInputData inputData);
}