package interface_adapter.blockblast;

import entity.blockblast.Board;
import entity.blockblast.GameState;
import use_case.blockblast.PlacePieceResponseModel;

public class BlockBlastPresenter {
    private final BlockBlastViewModel viewModel;
    public BlockBlastPresenter(BlockBlastViewModel viewModel){
        this.viewModel = viewModel;
    }
    public void prepareSuccessView(PlacePieceResponseModel responseModel){
        GameState endgamestate = responseModel.getGameState();
        Board endBoard = endgamestate.getBoard();
        int rows = endBoard.getRows();
        int cols = endBoard.getCols();
        boolean[][] boardCopy = new boolean[rows][cols];
        int score = endgamestate.getScore();
        boolean gameOver = endgamestate.isGameOver();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                boardCopy[r][c] = endBoard.isFilled(r, c);
            }
        }

        viewModel.setState(boardCopy, endgamestate.getScore(), endgamestate.isGameOver(), "");
    }
    public void prepareFailView(String message){
        viewModel.setState(viewModel.getBoard(), viewModel.getScore(), viewModel.isGameOver(), message);
    }
}
