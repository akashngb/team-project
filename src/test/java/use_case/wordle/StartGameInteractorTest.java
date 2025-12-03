package use_case.wordle;

import entity.wordle.WordleGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StartGameInteractorTest {

    private WordListGateway wordList;
    private GameSessionGateway sessionGateway;
    private WordleOutputBoundary presenter;
    private StartGameInteractor interactor;

    @BeforeEach
    void setUp() {
        wordList = mock(WordListGateway.class);
        sessionGateway = mock(GameSessionGateway.class);
        presenter = mock(WordleOutputBoundary.class);
        interactor = new StartGameInteractor(wordList, sessionGateway, presenter);
    }

    @Test
    void start_deterministicIndexWithinBounds_usesThatAnswer() {
        List<String> answers = Arrays.asList("alpha", "bravo", "cider");
        when(wordList.getAllAnswers()).thenReturn(answers);

        StartGameInputData req = new StartGameInputData("u1", true, 1);
        interactor.start(req);

        ArgumentCaptor<WordleGame> captor = ArgumentCaptor.forClass(WordleGame.class);
        verify(sessionGateway).save(eq("u1"), captor.capture());
        WordleGame saved = captor.getValue();
        assertEquals("bravo", saved.getAnswer());
        verify(presenter).presentStart(any());
    }

    @Test
    void start_deterministicIndexClamped_whenIndexTooLarge_usesLast() {
        List<String> answers = Arrays.asList("apple", "grape");
        when(wordList.getAllAnswers()).thenReturn(answers);

        StartGameInputData req = new StartGameInputData("u2", true, 99);
        interactor.start(req);

        ArgumentCaptor<WordleGame> captor = ArgumentCaptor.forClass(WordleGame.class);
        verify(sessionGateway).save(eq("u2"), captor.capture());
        WordleGame saved = captor.getValue();
        assertEquals("grape", saved.getAnswer());
        verify(presenter).presentStart(any());
    }

    @Test
    void start_nonDeterministic_usesPickAnswer() {
        when(wordList.pickAnswer()).thenReturn("zesty");

        StartGameInputData req = new StartGameInputData("u3", false, -1);
        interactor.start(req);

        ArgumentCaptor<WordleGame> captor = ArgumentCaptor.forClass(WordleGame.class);
        verify(sessionGateway).save(eq("u3"), captor.capture());
        WordleGame saved = captor.getValue();
        assertEquals("zesty", saved.getAnswer());
        verify(presenter).presentStart(any());
    }

    @Test
    void startGameInputData_singleArgConstructor_works() {
        StartGameInputData data = new StartGameInputData("userX");
        assertEquals("userX", data.getUserId());
        assertFalse(data.isDeterministic());
        assertEquals(-1, data.getAnswerIndex());
    }

    @Test
    void start_deterministicNegativeIndex_usesPickAnswer() {
        when(wordList.pickAnswer()).thenReturn("zebra"); // should hit else branch

        // deterministic = true, but negative index triggers else
        StartGameInputData req = new StartGameInputData("u5", true, -3);
        interactor.start(req);

        ArgumentCaptor<WordleGame> captor = ArgumentCaptor.forClass(WordleGame.class);
        verify(sessionGateway).save(eq("u5"), captor.capture());
        WordleGame saved = captor.getValue();
        assertEquals("zebra", saved.getAnswer());
        verify(presenter).presentStart(any());
    }

}
