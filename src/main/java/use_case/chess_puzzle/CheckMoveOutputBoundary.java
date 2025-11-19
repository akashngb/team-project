package use_case.chess_puzzle;

/**
 * Output boundary for move checking.
 */
public interface CheckMoveOutputBoundary {
    void presentCorrectMove(CheckMoveOutputData outputData);
    void presentIncorrectMove(String feedback);
    void presentPuzzleSolved();
}