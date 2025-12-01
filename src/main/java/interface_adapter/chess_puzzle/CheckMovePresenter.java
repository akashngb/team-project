package interface_adapter.chess_puzzle;

import use_case.chess_puzzle.CheckMoveOutputBoundary;
import use_case.chess_puzzle.CheckMoveOutputData;

/**
 * Presenter for move checking.
 */
public class CheckMovePresenter implements CheckMoveOutputBoundary {
    private final ChessPuzzleViewModel viewModel;

    public CheckMovePresenter(ChessPuzzleViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentCorrectMove(CheckMoveOutputData outputData) {
        ChessPuzzleState state = viewModel.getState();
        state.setFeedback(outputData.getFeedback());
        viewModel.firePropertyChange("move_result");
    }

    @Override
    public void presentIncorrectMove(String feedback) {
        ChessPuzzleState state = viewModel.getState();
        state.setFeedback(feedback);
        viewModel.firePropertyChange("move_result");
    }

    @Override
    public void presentPuzzleSolved() {
        ChessPuzzleState state = viewModel.getState();
        state.setFeedback("Puzzle solved! Great job!");
        viewModel.firePropertyChange("puzzle_solved");
    }
}
