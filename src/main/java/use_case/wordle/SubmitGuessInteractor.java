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
            WordleOutputData out = new WordleOutputData(
                    game.getGuesses(), // preserve all previous guesses
                    WordleGame.MAX_ATTEMPTS - game.getGuesses().size(),
                    game.isFinished(),
                    game.isWon(),
                    null, // keep the answer hidden
                    "Guess must be " + WordleGame.WORD_LENGTH + " letters." // only message
            );
            presenter.presentGuessResult(out); // update message but keep board intact
            return;
        }


        if (!wordList.isValidWord(guess)) {
            WordleGame gameState = game; // keep current guesses
            WordleOutputData out = new WordleOutputData(
                    gameState.getGuesses(), // keep all past guesses
                    WordleGame.MAX_ATTEMPTS - gameState.getGuesses().size(),
                    gameState.isFinished(),
                    gameState.isWon(),
                    null, // answer hidden
                    "'" + guess + "' is not a valid word."
            );
            presenter.presentGuessResult(out); // update view modle
            return;
        }

        try {
            Guess g = game.submitGuess(guess);

            if (game.isFinished()) {

                if (game.isWon()) {
                    int guessesUsed = game.getGuesses().size();
                    int points = Math.max(0, 6 - guessesUsed);
                    sessionGateway.addScore(userId, points);
                } else {
                    // Penalty for losing
                    sessionGateway.addScore(userId, -1); // subtract 1 point
                }

                sessionGateway.remove(userId);
            }
            sessionGateway.save(userId, game);

            WordleOutputData out = new WordleOutputData(
                    game.getGuesses(),
                    WordleGame.MAX_ATTEMPTS - game.getGuesses().size(),
                    game.isFinished(),
                    game.isWon(),
                    game.isFinished() ? game.getAnswer() : null,
                    "Guess accepted"
            );

            if (game.isFinished()) {
                if (game.isWon()) {
                    int guessesUsed = game.getGuesses().size();
                    int points = Math.max(0, 6 - guessesUsed);
                    sessionGateway.addScore(userId, points);
                } else {
                    sessionGateway.addScore(userId, 0);
                }
            }

            if (game.isFinished()) {
                if (game.isWon()) {
                    int guessesUsed = game.getGuesses().size();
                    int points = Math.max(0, 6 - guessesUsed);
                    sessionGateway.addScore(userId, points);
                } else {
                    sessionGateway.addScore(userId, 0);
                }
            }

            presenter.presentGuessResult(out);

            if (game.isFinished()) {
                sessionGateway.remove(userId);
            }




        } catch (Exception e) {
            presenter.presentError("Error submitting guess: " + e.getMessage());
        }
    }
}
