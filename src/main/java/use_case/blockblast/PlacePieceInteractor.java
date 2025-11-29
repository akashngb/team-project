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

        if (idx < 0 || idx >= pieces.length || pieces[idx] == null) {
            presenter.prepareFailView("Invalid piece index");
            return;
        }

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

        if (!validMove(gameState.getBoard(), gameState.getCurrentPieces())) {
            System.out.println("No valid move → Game Over");
            gameState.setGameOver(true);
        }

        presenter.prepareSuccessView(new PlacePieceResponseModel(gameState));
    }

    @Override
    public void newGame() {
        Board board = gameState.getBoard();
        board.clear();

        Piece[] pieces = gameState.getCurrentPieces();
        for (int i = 0; i < pieces.length; i++) {
            pieces[i] = generator.generateRandomPiece();
        }
        gameState.setCurrentPieces(pieces);

        gameState.setScore(0);
        gameState.setGameOver(false);

        presenter.prepareSuccessView(new PlacePieceResponseModel(gameState));
    }

    private boolean allPiecesUsed(Piece[] pieces) {
        for (Piece p : pieces) {
            if (p != null) return false;
        }
        return true;
    }

    private boolean validMove(Board board, Piece[] pieces) {
        int rows = board.getRows();
        int cols = board.getCols();
        for (Piece p : pieces) {
            if (p == null) continue;
            for (int r = 0; r < board.getRows(); r++) {
                for (int c = 0; c < board.getCols(); c++) {
                    boolean canPlace = true;
                    for (var cell : p.getCells()) {
                        int rr = r + cell.row;
                        int cc = c + cell.col;
                        if (rr < 0 || rr >= rows || cc < 0 || cc >= cols) {
                            canPlace = false;
                            break;
                        }

                        if (board.isFilled(rr, cc)) {
                            canPlace = false;
                            break;
                        }
                    }
                    if (canPlace) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}




