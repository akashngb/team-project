
import org.junit.jupiter.api.Test;
import use_case.chess_puzzle.CheckMoveInputData;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class CheckMoveInputDataTest {

    @Test
    void testConstructorAndGetMove() {
        CheckMoveInputData data = new CheckMoveInputData("e4", 5);

        // Test getter
        assertEquals("e4", data.getMove());
    }

    @Test
    void testMoveIndexStoredViaReflection() throws Exception {
        CheckMoveInputData data = new CheckMoveInputData("Nc3", 7);

        // Access private moveIndex using reflection
        Field moveIndexField = CheckMoveInputData.class.getDeclaredField("moveIndex");
        moveIndexField.setAccessible(true);

        int storedIndex = (int) moveIndexField.get(data);

        assertEquals(7, storedIndex);
    }

    @Test
    void testMoveCanBeNull() {
        CheckMoveInputData data = new CheckMoveInputData(null, 0);
        assertNull(data.getMove()); // Should not throw NullPointerException
    }
}
