package interface_adapter.chess_puzzle;

import use_case.chess_puzzle.LoadPuzzlesInputBoundary;
import use_case.chess_puzzle.LoadPuzzlesInputData;

/**
 * Controller for loading puzzles.
 */
public class LoadPuzzlesController {
    private final LoadPuzzlesInputBoundary interactor;

    public LoadPuzzlesController(LoadPuzzlesInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(int count, int rating) {
        LoadPuzzlesInputData inputData = new LoadPuzzlesInputData(count, rating);
        interactor.execute(inputData);
    }
}