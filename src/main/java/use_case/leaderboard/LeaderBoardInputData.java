package use_case.leaderboard;

/**
 * Receives a username and a score to be processed by the LeaderBoard interactor.
 */
public class LeaderBoardInputData {
    private final String username;
    private final Integer score;
    private final String gameName;

    public LeaderBoardInputData(String username, Integer score, String gameName) {
        this.username = username;
        this.score = score;
        this.gameName = gameName;
    }

    String getUsername() {
        return username;
    }

    Integer getScore() {
        return score;
    }

    String getGameName() {
        return gameName;
    }
}
