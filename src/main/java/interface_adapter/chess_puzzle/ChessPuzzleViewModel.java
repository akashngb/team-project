package interface_adapter.chess_puzzle;

import interface_adapter.ViewModel;

/**
 * View model for chess puzzles.
 */
public class ChessPuzzleViewModel extends ViewModel<ChessPuzzleState> {

    public ChessPuzzleViewModel() {
        super("chess puzzle");
        setState(new ChessPuzzleState());
    }
}