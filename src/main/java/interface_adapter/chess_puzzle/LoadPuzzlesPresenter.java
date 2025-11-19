package interface_adapter.chess_puzzle;

import use_case.chess_puzzle.LoadPuzzlesOutputBoundary;
import use_case.chess_puzzle.LoadPuzzlesOutputData;

/**
 * Presenter for loading puzzles.
 */
public class LoadPuzzlesPresenter implements LoadPuzzlesOutputBoundary {
    private final ChessPuzzleViewModel viewModel;

    public LoadPuzzlesPresenter(ChessPuzzleViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentPuzzles(LoadPuzzlesOutputData outputData) {
        ChessPuzzleState state = viewModel.getState();
        state.setPuzzles(outputData.getPuzzles());
        state.setCurrentPuzzleIndex(0);
        state.setErrorMessage(null);
        state.setFeedback("Puzzle loaded. Make your move!");
        viewModel.firePropertyChange();
    }

    @Override
    public void presentError(String error) {
        ChessPuzzleState state = viewModel.getState();
        state.setErrorMessage(error);
        viewModel.firePropertyChange();
    }
}