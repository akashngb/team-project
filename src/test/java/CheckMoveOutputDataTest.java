import use_case.chess_puzzle.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CheckMoveOutputDataTest {

    @Test
    void testConstructorAndGetters_AllTrueValues() {
        String feedback = "Good move!";
        boolean isCorrect = true;
        boolean puzzleSolved = true;
        String computerMove = "Qh5#";

        CheckMoveOutputData data = new CheckMoveOutputData(feedback, isCorrect, puzzleSolved, computerMove);

        assertEquals(feedback, data.getFeedback());
        assertEquals(feedback, data.getMessage());  // Same field returned
        assertTrue(data.getIsCorrect());
        assertTrue(data.isPuzzleSolved());
        assertEquals(computerMove, data.getComputerMove());
    }

    @Test
    void testConstructorAndGetters_AllFalseValues() {
        String feedback = "Incorrect move.";
        boolean isCorrect = false;
        boolean puzzleSolved = false;
        String computerMove = "Nc6";

        CheckMoveOutputData data = new CheckMoveOutputData(feedback, isCorrect, puzzleSolved, computerMove);

        assertEquals(feedback, data.getFeedback());
        assertEquals(feedback, data.getMessage());
        assertFalse(data.getIsCorrect());
        assertFalse(data.isPuzzleSolved());
        assertEquals(computerMove, data.getComputerMove());
    }

    @Test
    void testNullValues() {
        CheckMoveOutputData data = new CheckMoveOutputData(null, false, false, null);

        assertNull(data.getFeedback());
        assertNull(data.getMessage());
        assertNull(data.getComputerMove());
        assertFalse(data.getIsCorrect());
        assertFalse(data.isPuzzleSolved());
    }
}
