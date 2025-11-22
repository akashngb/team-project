package interface_adapter.wordle;

import use_case.wordle.WordleOutputBoundary;
import use_case.wordle.WordleOutputData;
import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.*;
import wordle.BoardPanel;
import wordle.WordleView;

/**
 * Presenter maps use-case output data into the WordleViewModel and pushes it to the UI via a callback.
 * The UI wiring (AppBuilder) should provide a Consumer<WordleViewModel> that sets the model in the ViewManager.
 */
public class WordlePresenter implements WordleOutputBoundary {

    private final Consumer<WordleViewModel> viewUpdater;

    public WordlePresenter(Consumer<WordleViewModel> viewUpdater) {
        this.viewUpdater = Objects.requireNonNull(viewUpdater);
    }

    @Override
    public void presentStart(WordleOutputData data) {
        WordleViewModel vm = new WordleViewModel(data.guesses, data.attemptsLeft, data.finished, data.won, data.answerIfFinished, data.message);
        viewUpdater.accept(vm);
    }

    @Override
    public void presentGuessResult(WordleOutputData data) {
        WordleViewModel vm = new WordleViewModel(data.guesses, data.attemptsLeft, data.finished, data.won, data.answerIfFinished, data.message);
        viewUpdater.accept(vm);
    }

    @Override
    public void presentError(String message) {
        // Keep UI simple: present an empty model with message or a model notifier.
        WordleViewModel vm = new WordleViewModel(java.util.Collections.emptyList(), 0, false, false, null, message);
        viewUpdater.accept(vm);
    }
}
