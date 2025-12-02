package use_case.blockblast;

import entity.blockblast.Board;
import entity.blockblast.GameState;
import entity.blockblast.Piece;
import entity.blockblast.PieceColor;
import entity.blockblast.Position;
import entity.blockblast.PieceGenerator;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PlacePieceInteractor.
 * The goal of this test suite is to reach 100% line coverage on
 * PlacePieceInteractor, including all branches in execute(), newGame()
 * and validMove().
 */
public class PlacePieceInteractorTest {

    private static class TestPresenter implements PlacePieceOutputBoundary {
        String errorMessage;
        PlacePieceResponseModel lastResponse;

        @Override
        public void prepareFailView(String msg) {
            this.errorMessage = msg;
        }

        @Override
        public void prepareSuccessView(PlacePieceResponseModel response) {
            this.lastResponse = response;
        }
    }

    private static class CountingGenerator extends PieceGenerator {
        private final Piece piece;
        int calls = 0;

        CountingGenerator(Piece piece) {
            this.piece = piece;
        }

        @Override
        public Piece generateRandomPiece() {
            calls++;
            return piece;
        }
    }

    /** Helper: build a 1×1 piece located at (0,0). */
    private static Piece singleCellPiece(PieceColor color) {
        List<Position> cells = new ArrayList<>();
        cells.add(new Position(0, 0));
        return new Piece(cells, color);
    }

    /**
     * Helper: build a 2-cell vertical piece with cells at (0,0) and (1,0).
     * This is useful for exercising the boundary checks inside validMove()
     * (rr >= rows).
     */
    private static Piece verticalTwoPiece(PieceColor color) {
        List<Position> cells = new ArrayList<>();
        cells.add(new Position(0, 0));
        cells.add(new Position(1, 0));
        return new Piece(cells, color);
    }

    /** Helper: build a 2x2 square piece using cells (0,0), (0,1), (1,0), (1,1). */
    private static Piece squareTwoByTwo(PieceColor color) {
        List<Position> cells = new ArrayList<>();
        cells.add(new Position(0, 0));
        cells.add(new Position(0, 1));
        cells.add(new Position(1, 0));
        cells.add(new Position(1, 1));
        return new Piece(cells, color);
    }

    /** Correct Path **/
    @Test
    public void execute_successfulPlacement_updatesBoardScoreAndPieces() {
        Board board = new Board(8, 8);
        Piece[] pieces = new Piece[] {
                singleCellPiece(PieceColor.RED),
                singleCellPiece(PieceColor.BLUE),
                singleCellPiece(PieceColor.GREEN)
        };
        GameState gameState = new GameState(board, pieces, 0, false);

        TestPresenter presenter = new TestPresenter();
        PieceGenerator generator = new PieceGenerator(); // real generator is fine
        PlacePieceInteractor interactor =
                new PlacePieceInteractor(gameState, generator, presenter);

        PlacePieceRequestModel request =
                new PlacePieceRequestModel(0, 0, 0);
        interactor.execute(request);

        assertTrue(board.isFilled(0, 0));

        assertNull(gameState.getCurrentPieces()[0]);

        assertEquals(0, gameState.getScore());

        assertFalse(gameState.isGameOver());
        assertNotNull(presenter.lastResponse);
        assertNull(presenter.errorMessage);
    }

    /** Error path: invalid piece index triggers fail view and returns early. */
    @Test
    public void execute_withInvalidIndex_callsFailView() {
        Board board = new Board(8, 8);
        Piece[] pieces = new Piece[] {
                singleCellPiece(PieceColor.RED),
                singleCellPiece(PieceColor.BLUE),
                singleCellPiece(PieceColor.GREEN)
        };
        GameState gameState = new GameState(board, pieces, 0, false);

        TestPresenter presenter = new TestPresenter();
        PieceGenerator generator = new PieceGenerator();
        PlacePieceInteractor interactor =
                new PlacePieceInteractor(gameState, generator, presenter);

        PlacePieceRequestModel request =
                new PlacePieceRequestModel(-1, 0, 0);
        interactor.execute(request);

        assertEquals("Invalid piece index", presenter.errorMessage);
        assertNull(presenter.lastResponse);
        assertEquals(0, gameState.getScore());
        assertFalse(gameState.isGameOver());
    }

    /** Error path: board cannot place the piece at the requested location. */
    @Test
    public void execute_whenBoardCannotPlace_callsFailView() {
        Board board = new Board(4, 4);
        board.place(singleCellPiece(PieceColor.RED), 0, 0);

        Piece[] pieces = new Piece[] {
                singleCellPiece(PieceColor.BLUE),
                singleCellPiece(PieceColor.YELLOW),
                singleCellPiece(PieceColor.GREEN)
        };
        GameState gameState = new GameState(board, pieces, 0, false);

        TestPresenter presenter = new TestPresenter();
        PieceGenerator generator = new PieceGenerator();
        PlacePieceInteractor interactor =
                new PlacePieceInteractor(gameState, generator, presenter);

        PlacePieceRequestModel request =
                new PlacePieceRequestModel(0, 0, 0);
        interactor.execute(request);

        assertEquals("Place not possible", presenter.errorMessage);
        assertNull(presenter.lastResponse);

        assertTrue(board.isFilled(0, 0));
        assertEquals(0, gameState.getScore());
    }

    /** Error path: if the game is already over, we do nothing except show message. */
    @Test
    public void execute_whenGameAlreadyOver_doesNotChangeState() {
        Board board = new Board(4, 4);
        Piece[] pieces = new Piece[] {
                singleCellPiece(PieceColor.RED),
                singleCellPiece(PieceColor.BLUE),
                singleCellPiece(PieceColor.GREEN)
        };
        GameState gameState = new GameState(board, pieces, 100, true); // already over

        TestPresenter presenter = new TestPresenter();
        PieceGenerator generator = new PieceGenerator();
        PlacePieceInteractor interactor =
                new PlacePieceInteractor(gameState, generator, presenter);

        PlacePieceRequestModel request =
                new PlacePieceRequestModel(0, 0, 0);
        interactor.execute(request);

        assertEquals("Game over.", presenter.errorMessage);
        assertEquals(100, gameState.getScore());
        assertTrue(gameState.isGameOver());
        assertTrue(board.getRows() > 0); // board still exists and unchanged
    }

    /**
     * After placing all three pieces, allPiecesUsed(...) should return true
     * and the interactor should call the generator to repopulate the array.
     */
    @Test
    public void execute_afterUsingAllPieces_generatesNewPieces() {
        Board board = new Board(8, 8);
        Piece p0 = singleCellPiece(PieceColor.RED);
        Piece p1 = singleCellPiece(PieceColor.BLUE);
        Piece p2 = singleCellPiece(PieceColor.GREEN);

        Piece[] pieces = new Piece[] { p0, p1, p2 };
        GameState gameState = new GameState(board, pieces, 0, false);

        Piece newPiece = singleCellPiece(PieceColor.YELLOW);
        CountingGenerator generator = new CountingGenerator(newPiece);
        TestPresenter presenter = new TestPresenter();

        PlacePieceInteractor interactor =
                new PlacePieceInteractor(gameState, generator, presenter);

        interactor.execute(new PlacePieceRequestModel(0, 0, 0));
        interactor.execute(new PlacePieceRequestModel(1, 0, 1));
        interactor.execute(new PlacePieceRequestModel(2, 0, 2));

        assertEquals(3, generator.calls);

        Piece[] current = gameState.getCurrentPieces();
        assertNotNull(current[0]);
        assertNotNull(current[1]);
        assertNotNull(current[2]);
    }

    /**
     * newGame() should clear the board, regenerate pieces, reset score,
     * and set gameOver to false.
     */
    @Test
    public void newGame_resetsBoardPiecesAndScore() {
        Board board = new Board(4, 4);
        // Put something on the board so we can see it get cleared
        board.place(singleCellPiece(PieceColor.RED), 0, 0);

        Piece[] pieces = new Piece[] {
                singleCellPiece(PieceColor.YELLOW),
                null,
                null
        };
        GameState gameState = new GameState(board, pieces, 50, true);

        Piece newPiece = singleCellPiece(PieceColor.BLUE);
        CountingGenerator generator = new CountingGenerator(newPiece);
        TestPresenter presenter = new TestPresenter();

        PlacePieceInteractor interactor =
                new PlacePieceInteractor(gameState, generator, presenter);

        interactor.newGame();

        // Board should be completely empty
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                assertFalse(board.isFilled(r, c),
                        "Board should be empty after newGame()");
            }
        }

        for (Piece p : gameState.getCurrentPieces()) {
            assertNotNull(p);
        }

        // Score and gameOver reset
        assertEquals(0, gameState.getScore());
        assertFalse(gameState.isGameOver());

        // Success view called
        assertNotNull(presenter.lastResponse);
        // Generator should be called exactly pieces.length times
        assertEquals(gameState.getCurrentPieces().length, generator.calls);
    }

    /**
     * This test drives the interactor into a situation where
     * validMove(...) returns false – i.e., no valid move remains.
     * That should set gameOver = true.
     */
    @Test
    public void execute_setsGameOverWhenNoValidMovesRemain() {

        Board board = new Board(2, 2);
        board.place(singleCellPiece(PieceColor.RED), 0, 0);

        Piece p0 = singleCellPiece(PieceColor.YELLOW);
        Piece p1 = squareTwoByTwo(PieceColor.BLUE);
        Piece p2 = squareTwoByTwo(PieceColor.GREEN);

        Piece[] pieces = new Piece[] { p0, p1, p2 };
        GameState gameState = new GameState(board, pieces, 0, false);

        TestPresenter presenter = new TestPresenter();
        PieceGenerator generator = new PieceGenerator();
        PlacePieceInteractor interactor =
                new PlacePieceInteractor(gameState, generator, presenter);

        PlacePieceRequestModel request =
                new PlacePieceRequestModel(0, 1, 1);
        interactor.execute(request);

        assertNull(presenter.errorMessage);
        assertTrue(gameState.isGameOver(),
                "Game should be over when no valid move remains");
    }
}
