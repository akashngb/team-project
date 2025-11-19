package use_case.chess_puzzle;

/**
 * Output boundary for loading chess puzzles.
 */
public interface LoadPuzzlesOutputBoundary {
    void presentPuzzles(LoadPuzzlesOutputData outputData);
    void presentError(String error);
}