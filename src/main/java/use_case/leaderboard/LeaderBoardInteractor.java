package use_case.leaderboard;

import entity.User;

import java.util.List;
import java.util.Map;

public class LeaderBoardInteractor implements LeaderBoardInputBoundary {
    private final LeaderBoardDataAccessInterface leaderBoardDataAccessObject;
    private final LeaderBoardOutputBoundary leaderBoardPresenter;

    public LeaderBoardInteractor(LeaderBoardDataAccessInterface leaderBoardDataAccessObject,
                                 LeaderBoardOutputBoundary leaderBoardPresenter) {
        this.leaderBoardDataAccessObject = leaderBoardDataAccessObject;
        this.leaderBoardPresenter = leaderBoardPresenter;
    }

    @Override
    public void execute(LeaderBoardInputData leaderBoardInputData) {
        final String username = leaderBoardInputData.getUsername();
        final Integer score = leaderBoardInputData.getScore();
        final String gameName = leaderBoardInputData.getGameName();

        if (!leaderBoardDataAccessObject.existsByName(username)) {
            leaderBoardPresenter.prepareFailView(username + ": Account does not exist.");
        } else {
            Map<String, Integer> currentHighscores = leaderBoardDataAccessObject.getHighscoresByName(username);
            Integer previousHighscore = currentHighscores.getOrDefault(gameName, 0);
            if (score > previousHighscore) {
                leaderBoardDataAccessObject.changeHighscore(username, gameName, score);
                List<User> topUsers = leaderBoardDataAccessObject.getTopUsersForGame(gameName, 10);
                leaderBoardPresenter.prepareSuccessView(
                        new LeaderBoardOutputData(topUsers, true, true, gameName)
                );
            } else {
                List<User> topUsers = leaderBoardDataAccessObject.getTopUsersForGame(gameName, 10);
                leaderBoardPresenter.prepareSuccessView(
                        new LeaderBoardOutputData(topUsers, false, true, gameName)
                );
            }
        }
    }
}
