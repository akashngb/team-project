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
    void start_deterministicIndexWithinBounds() {
        List<String> answers = List.of("alpha", "bravo", "cider");
        when(wordList.getAllAnswers()).thenReturn(answers);

        interactor.start(new StartGameInputData("u1", true, 1));

        ArgumentCaptor<WordleGame> captor = ArgumentCaptor.forClass(WordleGame.class);
        verify(sessionGateway).save(eq("u1"), captor.capture());
        assertEquals("bravo", captor.getValue().getAnswer());
        verify(presenter).presentStart(any());
    }

    @Test
    void start_deterministicIndexClamped() {
        List<String> answers = List.of("apple", "grape");
        when(wordList.getAllAnswers()).thenReturn(answers);

        interactor.start(new StartGameInputData("u2", true, 99));

        ArgumentCaptor<WordleGame> captor = ArgumentCaptor.forClass(WordleGame.class);
        verify(sessionGateway).save(eq("u2"), captor.capture());
        assertEquals("grape", captor.getValue().getAnswer());
        verify(presenter).presentStart(any());
    }

    @Test
    void start_nonDeterministic() {
        when(wordList.pickAnswer()).thenReturn("zesty");

        interactor.start(new StartGameInputData("u3", false, -1));

        ArgumentCaptor<WordleGame> captor = ArgumentCaptor.forClass(WordleGame.class);
        verify(sessionGateway).save(eq("u3"), captor.capture());
        assertEquals("zesty", captor.getValue().getAnswer());
        verify(presenter).presentStart(any());
    }
}
