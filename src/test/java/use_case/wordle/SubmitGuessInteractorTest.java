package use_case.wordle;

import entity.wordle.WordleGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubmitGuessInteractorTest {

    private WordListGateway wordList;
    private GameSessionGateway sessionGateway;
    private WordleOutputBoundary presenter;
    private SubmitGuessInteractor interactor;

    @BeforeEach
    void setUp() {
        wordList = mock(WordListGateway.class);
        sessionGateway = mock(GameSessionGateway.class);
        presenter = mock(WordleOutputBoundary.class);
        interactor = new SubmitGuessInteractor(wordList, sessionGateway, presenter);
    }

    @Test
    void getScore_returnsSessionScore() {
        when(sessionGateway.getScore("user1")).thenReturn(42);
        assertEquals(42, interactor.getScore("user1"));
    }

    @Test
    void submitGuess_noActiveGame_callsPresentError() {
        when(sessionGateway.load("u1")).thenReturn(null);
        interactor.submitGuess(new SubmitGuessInputData("u1", "apple"));
        verify(presenter).presentError("No active game — start a new game first.");
    }

    @Test
    void submitGuess_invalidOrNullGuess_callsPresentGuessResult() {
        WordleGame game = new WordleGame("crane");
        when(sessionGateway.load("u2")).thenReturn(game);

        // null guess
        interactor.submitGuess(new SubmitGuessInputData("u2", null));
        // wrong length
        interactor.submitGuess(new SubmitGuessInputData("u2", "ab"));

        ArgumentCaptor<WordleOutputData> captor = ArgumentCaptor.forClass(WordleOutputData.class);
        verify(presenter, times(2)).presentGuessResult(captor.capture());
        List<WordleOutputData> all = captor.getAllValues();
        assertTrue(all.get(0).message.contains("letters"));
        assertTrue(all.get(1).message.contains("letters"));
    }

    @Test
    void submitGuess_invalidWord_callsPresentGuessResult() {
        WordleGame game = new WordleGame("crane");
        when(sessionGateway.load("u3")).thenReturn(game);
        when(wordList.isValidWord("fizzy")).thenReturn(false);

        interactor.submitGuess(new SubmitGuessInputData("u3", "fizzy"));

        ArgumentCaptor<WordleOutputData> captor = ArgumentCaptor.forClass(WordleOutputData.class);
        verify(presenter).presentGuessResult(captor.capture());
        assertTrue(captor.getValue().message.contains("not a valid word"));
    }

    @Test
    void submitGuess_validUnfinishedGuess_savesAndPresents() {
        WordleGame game = new WordleGame("crane");
        when(sessionGateway.load("u4")).thenReturn(game);
        when(wordList.isValidWord("apple")).thenReturn(true);

        interactor.submitGuess(new SubmitGuessInputData("u4", "apple"));

        ArgumentCaptor<WordleOutputData> captor = ArgumentCaptor.forClass(WordleOutputData.class);
        verify(presenter).presentGuessResult(captor.capture());
        WordleOutputData out = captor.getValue();

        assertFalse(out.finished);
        assertNull(out.answerIfFinished);
        verify(sessionGateway).save(eq("u4"), any(WordleGame.class));
    }

    @Test
    void submitGuess_finishedWinningGame_addsScoreAndRemoves() {
        WordleGame mockGame = mock(WordleGame.class);
        when(sessionGateway.load("u5")).thenReturn(mockGame);
        when(wordList.isValidWord("apple")).thenReturn(true);
        when(mockGame.submitGuess("apple")).thenReturn(null);
        when(mockGame.isFinished()).thenReturn(true);
        when(mockGame.isWon()).thenReturn(true);
        when(mockGame.getGuesses()).thenReturn(List.of());
        when(mockGame.getAnswer()).thenReturn("apple");

        interactor.submitGuess(new SubmitGuessInputData("u5", "apple"));

        verify(sessionGateway).addScore("u5", WordleGame.MAX_ATTEMPTS - 0 + 1);
        verify(sessionGateway).remove("u5");
        verify(presenter).presentGuessResult(any());
    }

    @Test
    void submitGuess_finishedLosingGame_addsZeroAndRemoves() {
        WordleGame mockGame = mock(WordleGame.class);
        when(sessionGateway.load("u6")).thenReturn(mockGame);
        when(wordList.isValidWord("apple")).thenReturn(true);
        when(mockGame.submitGuess("apple")).thenReturn(null);
        when(mockGame.isFinished()).thenReturn(true);
        when(mockGame.isWon()).thenReturn(false);
        when(mockGame.getGuesses()).thenReturn(List.of());
        when(mockGame.getAnswer()).thenReturn("crane");

        interactor.submitGuess(new SubmitGuessInputData("u6", "apple"));

        verify(sessionGateway).addScore("u6", 0);
        verify(sessionGateway).remove("u6");
        verify(presenter).presentGuessResult(any());
    }

    @Test
    void submitGuess_exception_callsPresenterError() {
        WordleGame mockGame = mock(WordleGame.class);
        when(sessionGateway.load("u7")).thenReturn(mockGame);
        when(wordList.isValidWord("apple")).thenReturn(true);
        when(mockGame.submitGuess("apple")).thenThrow(new RuntimeException("boom"));

        interactor.submitGuess(new SubmitGuessInputData("u7", "apple"));

        verify(presenter).presentError(contains("boom"));
    }
}
