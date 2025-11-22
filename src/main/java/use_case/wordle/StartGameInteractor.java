package use_case.wordle;

import entity.wordle.WordleGame;

import java.util.List;
import java.util.Objects;

public class StartGameInteractor implements StartGameInputBoundary {
    private final WordListGateway wordList;
    private final GameSessionGateway sessionGateway;
    private final WordleOutputBoundary presenter;

    public StartGameInteractor(WordListGateway wordList, GameSessionGateway sessionGateway, WordleOutputBoundary presenter) {
        this.wordList = Objects.requireNonNull(wordList);
        this.sessionGateway = Objects.requireNonNull(sessionGateway);
        this.presenter = Objects.requireNonNull(presenter);
    }

    @Override
    public void start(StartGameInputData request) {
        String answer;
        if (request.isDeterministic() && request.getAnswerIndex() >= 0) {
            List<String> answers = wordList.getAllAnswers();
            int idx = Math.min(Math.max(0, request.getAnswerIndex()), answers.size() - 1);
            answer = answers.get(idx);
        } else {
            answer = wordList.pickAnswer();
        }
        WordleGame game = new WordleGame(answer);
        sessionGateway.save(request.getUserId(), game);

        WordleOutputData out = new WordleOutputData(game.getGuesses(), WordleGame.MAX_ATTEMPTS, false, false, null, "New game started");
        presenter.presentStart(out);
    }
}
