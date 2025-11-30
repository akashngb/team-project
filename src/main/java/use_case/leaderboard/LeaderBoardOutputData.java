package use_case.leaderboard;

import entity.User;

import java.util.List;

public class LeaderBoardOutputData {

    private final List<User> topUsers;
    private final boolean isNewHighscore;
    private final boolean isSuccess;

    public LeaderBoardOutputData(List<User> topUsers, boolean isNewHighscore, boolean isSuccess) {
        this.topUsers = topUsers;
        this.isNewHighscore = isNewHighscore;
        this.isSuccess = isSuccess;
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
}
