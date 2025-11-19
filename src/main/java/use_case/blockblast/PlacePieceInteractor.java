package use_case.blockblast;

import entity.blockblast.Board;
import entity.blockblast.GameState;
import entity.blockblast.Piece;
import entity.blockblast.PieceGenerator;

public class PlacePieceInteractor implements PlacePieceInputBoundary {
    private final GameState gameState;
    private final PieceGenerator generator;
    private final PlacePieceOutputBoundary presenter;

    public PlacePieceInteractor(GameState gameState, PieceGenerator generator, PlacePieceOutputBoundary presenter) {
        this.gameState = gameState;
        this.generator = generator;
        this.presenter = presenter;
    }


    @Override
    public void execute(PlacePieceRequestModel requestModel) {
        if (gameState.isGameOver()) {
            presenter.prepareFailView("Game over.");
            return;
        }

        Piece[] pieces = gameState.getCurrentPieces();
        int idx = requestModel.getPieceIndex();
        int row = requestModel.getRow();
        int col = requestModel.getCol();

        Piece piece = pieces[idx];
        Board board = gameState.getBoard();

        if (!board.canPlace(piece, row, col)) {
            presenter.prepareFailView("Place not possible");
            return;
        }

        board.place(piece, row, col);
        int cleared = board.clearFullLines();
        gameState.setScore(gameState.getScore() + cleared * 10);

        pieces[idx] = null;
        if (allPiecesUsed(pieces)) {
            for (int i = 0; i < pieces.length; i++) {
                pieces[i] = generator.generateRandomPiece();
            }
        }
        gameState.setCurrentPieces(pieces);
        if (!ValidMove(gameState.getBoard(), gameState.getCurrentPieces())) {
            gameState.setGameOver(true);
        }
    }


    private boolean allPiecesUsed(Piece[] pieces) {
        for (Piece p : pieces) {
            if (p != null) return false;
        }
        return true;
    }

    private boolean ValidMove(Board board, Piece[] pieces) {
        for (Piece p : pieces) {
            if (p == null) continue;
            for (int r = 0; r < board.getRows(); r++) {
                for (int c = 0; c < board.getCols(); c++) {
                    if (board.canPlace(p, r, c)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}




