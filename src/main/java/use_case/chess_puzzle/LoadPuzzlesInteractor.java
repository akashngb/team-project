package use_case.chess_puzzle;

import entity.ChessPuzzle;
import java.util.List;

/**
 * Interactor for loading chess puzzles.
 */
public class LoadPuzzlesInteractor implements LoadPuzzlesInputBoundary {
    private final ChessPuzzleDataAccessInterface dataAccess;
    private final LoadPuzzlesOutputBoundary presenter;

    public LoadPuzzlesInteractor(ChessPuzzleDataAccessInterface dataAccess,
                                 LoadPuzzlesOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(LoadPuzzlesInputData inputData) {
        try {
            List<ChessPuzzle> puzzles = dataAccess.fetchPuzzles(
                    inputData.getCount(),
                    inputData.getRating()
            );

            if (puzzles.isEmpty()) {
                presenter.presentError("No puzzles found");
            } else {
                LoadPuzzlesOutputData outputData = new LoadPuzzlesOutputData(puzzles);
                presenter.presentPuzzles(outputData);
            }
        } catch (Exception e) {
            presenter.presentError("Failed to load puzzles: " + e.getMessage());
        }
    }
}
