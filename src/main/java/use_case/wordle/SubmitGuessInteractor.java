package use_case.wordle;

import entity.wordle.Guess;
import entity.wordle.WordleGame;

import java.util.Objects;

public class SubmitGuessInteractor implements SubmitGuessInputBoundary {
    private final WordListGateway wordList;
    private final GameSessionGateway sessionGateway;
    private final WordleOutputBoundary presenter;

    public SubmitGuessInteractor(WordListGateway wordList, GameSessionGateway sessionGateway, WordleOutputBoundary presenter) {
        this.wordList = Objects.requireNonNull(wordList);
        this.sessionGateway = Objects.requireNonNull(sessionGateway);
        this.presenter = Objects.requireNonNull(presenter);
    }

    @Override
    public int getScore(String userId) {
        return sessionGateway.getScore(userId);
    }

    @Override
    public void submitGuess(SubmitGuessInputData request) {
        String userId = request.getUserId();
        WordleGame game = sessionGateway.load(userId);
        if (game == null) {
            presenter.presentError("No active game — start a new game first.");
            return;
        }
        String guess = request.getGuess();

        if (guess == null || guess.length() != WordleGame.WORD_LENGTH) {
            presenter.presentGuessResult(new WordleOutputData(
                    game.getGuesses(),
                    WordleGame.MAX_ATTEMPTS - game.getGuesses().size(),
                    game.isFinished(),
                    game.isWon(),
                    null,
                    "Guess must be " + WordleGame.WORD_LENGTH + " letters."
            ));
            return;
        }

        if (!wordList.isValidWord(guess)) {
            presenter.presentGuessResult(new WordleOutputData(
                    game.getGuesses(),
                    WordleGame.MAX_ATTEMPTS - game.getGuesses().size(),
                    game.isFinished(),
                    game.isWon(),
                    null,
                    "'" + guess + "' is not a valid word."
            ));
            return;
        }

        try {
            Guess g = game.submitGuess(guess);

            if (game.isFinished()) {
                int points = 0;
                if (game.isWon()) {
                    // Remaining guesses = MAX_ATTEMPTS - guessesUsed + 1
                    points = WordleGame.MAX_ATTEMPTS - game.getGuesses().size() + 1;
                }
                sessionGateway.addScore(userId, points);
                sessionGateway.remove(userId);
            } else {
                sessionGateway.save(userId, game);
            }

            presenter.presentGuessResult(new WordleOutputData(
                    game.getGuesses(),
                    WordleGame.MAX_ATTEMPTS - game.getGuesses().size(),
                    game.isFinished(),
                    game.isWon(),
                    game.isFinished() ? game.getAnswer() : null,
                    "Guess accepted"
            ));

        } catch (Exception e) {
            presenter.presentError("Error submitting guess: " + e.getMessage());
        }
    }

}
