package use_case.chess_puzzle;

import entity.ChessPuzzle;
import java.util.List;

/**
 * Output data for loading puzzles use case.
 */
public class LoadPuzzlesOutputData {
    private final List<ChessPuzzle> puzzles;

    public LoadPuzzlesOutputData(List<ChessPuzzle> puzzles) {
        this.puzzles = puzzles;
    }

    public List<ChessPuzzle> getPuzzles() {
        return puzzles;
    }
}
