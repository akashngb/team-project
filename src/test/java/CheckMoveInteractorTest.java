import org.junit.jupiter.api.Test;
import use_case.chess_puzzle.*;

import entity.ChessPuzzle;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CheckMoveInteractorTest {

    // ----------- STUB PRESENTER -----------
    static class StubPresenter implements CheckMoveOutputBoundary {
        String incorrectMessage;
        CheckMoveOutputData correctData;
        boolean puzzleSolvedCalled = false;

        @Override
        public void presentIncorrectMove(String message) {
            this.incorrectMessage = message;
        }

        @Override
        public void presentCorrectMove(CheckMoveOutputData data) {
            this.correctData = data;
        }

        @Override
        public void presentPuzzleSolved() {
            puzzleSolvedCalled = true;
        }
    }

    // ----------- STUB INPUT -----------
    static class StubInputData extends CheckMoveInputData {
        private final String move;

        public StubInputData(String move) {
            super("a1a1", 0);
            this.move = move;
        }

        @Override
        public String getMove() {
            return move;
        }
    }

    // ----------- TEST CASES -----------

    @Test
    void testNoPuzzleLoaded() {
        StubPresenter presenter = new StubPresenter();
        CheckMoveInteractor interactor = new CheckMoveInteractor(presenter);

        interactor.execute(new StubInputData("e4"));

        assertEquals("No puzzle loaded", presenter.incorrectMessage);
    }

    @Test
    void testCorrectMoveWithComputerResponseNotSolved() {
        StubPresenter presenter = new StubPresenter();
        CheckMoveInteractor interactor = new CheckMoveInteractor(presenter);

        ChessPuzzle puzzle = new ChessPuzzle(
                "p1", "fen", List.of("e4", "e5", "Nf3"),
                1200, List.of("tactic")
        );

        interactor.setCurrentPuzzle(puzzle);
        interactor.execute(new StubInputData("e4"));

        assertNotNull(presenter.correctData);
        assertEquals("e5", presenter.correctData.getComputerMove());
        assertFalse(presenter.puzzleSolvedCalled);
    }

    @Test
    void testCorrectMoveWithComputerResponsePuzzleSolved() {
        StubPresenter presenter = new StubPresenter();
        CheckMoveInteractor interactor = new CheckMoveInteractor(presenter);

        ChessPuzzle puzzle = new ChessPuzzle(
                "p2", "fen", List.of("e4", "e5"),
                1400, List.of("mate")
        );

        interactor.setCurrentPuzzle(puzzle);
        interactor.execute(new StubInputData("e4"));

        assertNotNull(presenter.correctData);
        assertEquals("Correct! Puzzle solved!", presenter.correctData.getMessage());
        assertEquals("e5", presenter.correctData.getComputerMove());
        assertTrue(presenter.correctData.isPuzzleSolved());
        assertTrue(presenter.puzzleSolvedCalled);
    }

    @Test
    void testCorrectMoveNoComputerResponsePuzzleSolved() {
        StubPresenter presenter = new StubPresenter();
        CheckMoveInteractor interactor = new CheckMoveInteractor(presenter);

        ChessPuzzle puzzle = new ChessPuzzle(
                "p3", "fen", List.of("e4"),
                1000, List.of("opening")
        );

        interactor.setCurrentPuzzle(puzzle);
        interactor.execute(new StubInputData("e4"));

        assertNotNull(presenter.correctData);
        assertNull(presenter.correctData.getComputerMove());
        assertTrue(presenter.puzzleSolvedCalled);
    }

    @Test
    void testIncorrectMove() {
        StubPresenter presenter = new StubPresenter();
        CheckMoveInteractor interactor = new CheckMoveInteractor(presenter);

        ChessPuzzle puzzle = new ChessPuzzle(
                "p4", "fen", List.of("e4"),
                900, List.of("fork")
        );

        interactor.setCurrentPuzzle(puzzle);
        interactor.execute(new StubInputData("d4"));

        assertEquals("Incorrect move. Try again!", presenter.incorrectMessage);
        assertNull(presenter.correctData);
    }

    @Test
    void testAlreadySolvedPuzzle() {
        StubPresenter presenter = new StubPresenter();
        CheckMoveInteractor interactor = new CheckMoveInteractor(presenter);

        ChessPuzzle puzzle = new ChessPuzzle(
                "p5", "fen", List.of("e4"),
                1000, List.of("pin")
        );

        interactor.setCurrentPuzzle(puzzle);

        // First correct move solves the puzzle
        interactor.execute(new StubInputData("e4"));

        // Reset presenter for second call
        presenter.correctData = null;
        presenter.incorrectMessage = null;

        // Now call again → should hit "already solved"
        interactor.execute(new StubInputData("anything"));

        assertTrue(presenter.puzzleSolvedCalled);
        assertNull(presenter.correctData);
        assertNull(presenter.incorrectMessage);
    }
}
