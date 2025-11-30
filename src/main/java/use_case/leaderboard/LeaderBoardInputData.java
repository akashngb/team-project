package use_case.leaderboard;

public class LeaderBoardInputData {
    private final String username;
    private final Integer score;

    public LeaderBoardInputData(String username, Integer score) {
        this.username = username;
        this.score = score;
    }

    String getUsername() {
        return username;
    }

    Integer getScore() {
        return score;
    }
}
