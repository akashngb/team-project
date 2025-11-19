package use_case.chess_puzzle;

import entity.ChessPuzzle;
import java.util.List;

/**
 * Interface for accessing chess puzzle data.
 */
public interface ChessPuzzleDataAccessInterface {
    /**
     * Fetches puzzles from the API.
     * @param count number of puzzles to fetch
     * @param rating difficulty rating
     * @return list of chess puzzles
     */
    List<ChessPuzzle> fetchPuzzles(int count, int rating);
}