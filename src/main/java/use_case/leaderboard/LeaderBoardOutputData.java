package use_case.leaderboard;

import entity.User;

import java.util.List;

public class LeaderBoardOutputData {

    private final List<User> topUsers;
    private final boolean isNewHighscore;
    private final boolean isSuccess;
    private final String gameName;

    public LeaderBoardOutputData(List<User> topUsers, boolean isNewHighscore, boolean isSuccess, String gameName) {
        this.topUsers = topUsers;
        this.isNewHighscore = isNewHighscore;
        this.isSuccess = isSuccess;
        this.gameName = gameName;
    }

    public List<User> getTopUsers() {
        return topUsers;
    }

    public boolean isNewHighscore() {
        return isNewHighscore;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getGameName() {
        return gameName;
    }
}
