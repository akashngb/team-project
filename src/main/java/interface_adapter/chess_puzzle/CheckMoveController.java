package interface_adapter.chess_puzzle;

import use_case.chess_puzzle.CheckMoveInputBoundary;
import use_case.chess_puzzle.CheckMoveInputData;

/**
 * Controller for checking moves.
 */
public class CheckMoveController {
    private final CheckMoveInputBoundary interactor;

    public CheckMoveController(CheckMoveInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String move, int moveIndex) {
        CheckMoveInputData inputData = new CheckMoveInputData(move, moveIndex);
        interactor.execute(inputData);
    }
}
