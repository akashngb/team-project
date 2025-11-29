package interface_adapter.blockblast;

import entity.blockblast.Board;
import entity.blockblast.GameState;
import entity.blockblast.PieceColor;
import use_case.blockblast.PlacePieceOutputBoundary;
import use_case.blockblast.PlacePieceResponseModel;

public class BlockBlastPresenter implements PlacePieceOutputBoundary {
    private final BlockBlastViewModel viewModel;
    public BlockBlastPresenter(BlockBlastViewModel viewModel){
        this.viewModel = viewModel;
    }
    public void prepareSuccessView(PlacePieceResponseModel responseModel){
        GameState endgamestate = responseModel.getGameState();
        Board endBoard = endgamestate.getBoard();
        int rows = endBoard.getRows();
        int cols = endBoard.getCols();
        PieceColor[][] colorGrid = endBoard.getGrid();
        boolean[][] occupied = new boolean[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                occupied[r][c] = (colorGrid[r][c] != null);
            }
        }
        int score = endgamestate.getScore();
        boolean gameOver = endgamestate.isGameOver();

        viewModel.setState(occupied, colorGrid, endgamestate.getScore(), endgamestate.isGameOver(), "", endgamestate.getCurrentPieces());
    }
    public void prepareFailView(String message){
        viewModel.setState(viewModel.getBoard(), viewModel.getCellColors(), viewModel.getScore(), viewModel.isGameOver(), message, viewModel.getPieces());
    }
}
