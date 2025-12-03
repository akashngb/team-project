
import entity.ChessPuzzle;
import org.junit.jupiter.api.Test;
import use_case.chess_puzzle.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoadPuzzlesInteractorTest {

    /**
     * Fake Data Access for controlling test behaviour.
     */
    static class FakeDataAccess implements ChessPuzzleDataAccessInterface {
        List<ChessPuzzle> returnList = new ArrayList<>();
        boolean throwException = false;

        @Override
        public List<ChessPuzzle> fetchPuzzles(int count, int rating) {
            if (throwException) {
                throw new RuntimeException("API Error");
            }
            return returnList;
        }
    }

    /**
     * Fake presenter to capture results from interactor.
     */
    static class FakePresenter implements LoadPuzzlesOutputBoundary {
        LoadPuzzlesOutputData receivedData = null;
        String receivedError = null;

        @Override
        public void presentPuzzles(LoadPuzzlesOutputData outputData) {
            this.receivedData = outputData;
        }

        @Override
        public void presentError(String error) {
            this.receivedError = error;
        }
    }

    /**
     * Utility — simple dummy puzzle instance.
     */
    private ChessPuzzle createPuzzle() {
        return new ChessPuzzle(
                "p1",
                "8/8/8/8/8/8/8/8 w - - 0 1",
                List.of("a1"),
                1200,
                List.of("theme")
        );
    }

    @Test
    void testPuzzlesLoadedSuccessfully() {
        FakeDataAccess dataAccess = new FakeDataAccess();
        FakePresenter presenter = new FakePresenter();

        dataAccess.returnList = List.of(createPuzzle());

        LoadPuzzlesInteractor interactor = new LoadPuzzlesInteractor(dataAccess, presenter);
        LoadPuzzlesInputData input = new LoadPuzzlesInputData(5, 1500);

        interactor.execute(input);

        assertNotNull(presenter.receivedData);
        assertNull(presenter.receivedError);
        assertEquals(1, presenter.receivedData.getPuzzles().size());
        assertEquals("p1", presenter.receivedData.getPuzzles().get(0).getPuzzleId());
    }

    @Test
    void testNoPuzzlesFound() {
        FakeDataAccess dataAccess = new FakeDataAccess();
        FakePresenter presenter = new FakePresenter();

        dataAccess.returnList = List.of(); // empty list

        LoadPuzzlesInteractor interactor = new LoadPuzzlesInteractor(dataAccess, presenter);
        LoadPuzzlesInputData input = new LoadPuzzlesInputData(10, 2000);

        interactor.execute(input);

        assertNull(presenter.receivedData);
        assertEquals("No puzzles found", presenter.receivedError);
    }

    @Test
    void testExceptionThrown() {
        FakeDataAccess dataAccess = new FakeDataAccess();
        FakePresenter presenter = new FakePresenter();

        dataAccess.throwException = true;

        LoadPuzzlesInteractor interactor = new LoadPuzzlesInteractor(dataAccess, presenter);
        LoadPuzzlesInputData input = new LoadPuzzlesInputData(3, 1000);

        interactor.execute(input);

        assertNull(presenter.receivedData);
        assertNotNull(presenter.receivedError);
        assertTrue(presenter.receivedError.contains("Failed to load puzzles"));
        assertTrue(presenter.receivedError.contains("API Error"));
    }
}
