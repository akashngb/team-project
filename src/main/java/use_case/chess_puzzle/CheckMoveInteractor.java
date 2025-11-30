package use_case.chess_puzzle;

import entity.ChessPuzzle;
import java.util.List;

/**
 * Interactor for checking chess moves.
 */
public class CheckMoveInteractor implements CheckMoveInputBoundary {
    private final CheckMoveOutputBoundary presenter;
    private ChessPuzzle currentPuzzle;
    private int currentMoveIndex;

    public CheckMoveInteractor(CheckMoveOutputBoundary presenter) {
        this.presenter = presenter;
        this.currentMoveIndex = 0;
    }

    public void setCurrentPuzzle(ChessPuzzle puzzle) {
        this.currentPuzzle = puzzle;
        this.currentMoveIndex = 0;
    }

    @Override
    public void execute(CheckMoveInputData inputData) {
        if (currentPuzzle == null) {
            presenter.presentIncorrectMove("No puzzle loaded");
            return;
        }

        List<String> solution = currentPuzzle.getSolutionMoves();
        String playerMove = inputData.getMove();

        if (currentMoveIndex >= solution.size()) {
            presenter.presentPuzzleSolved();
            return;
        }

        String correctMove = solution.get(currentMoveIndex);

        if (playerMove.equals(correctMove)) {
            currentMoveIndex++;

            // Check if there's a computer response move
            if (currentMoveIndex < solution.size()) {
                String computerMove = solution.get(currentMoveIndex);
                currentMoveIndex++;

                // Check if puzzle is now solved
                if (currentMoveIndex >= solution.size()) {
                    CheckMoveOutputData outputData = new CheckMoveOutputData(
                            "Correct! Puzzle solved!", true, true, computerMove
                    );
                    presenter.presentCorrectMove(outputData);
                    presenter.presentPuzzleSolved();
                } else {
                    CheckMoveOutputData outputData = new CheckMoveOutputData(
                            "Correct! Continue...", true, false, computerMove
                    );
                    presenter.presentCorrectMove(outputData);
                }
            } else {
                // No computer response, puzzle solved
                CheckMoveOutputData outputData = new CheckMoveOutputData(
                        "Correct! Puzzle solved!", true, true, null
                );
                presenter.presentCorrectMove(outputData);
                presenter.presentPuzzleSolved();
            }
        } else {
            presenter.presentIncorrectMove("Incorrect move. Try again!");
        }
    }
}
