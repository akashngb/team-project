package interface_adapter.leaderboard;

import entity.User;

import java.util.ArrayList;
import java.util.List;

public class LeaderBoardState {
    private String errorMessage = "";
    private List<User> topUsers = new ArrayList<>();
    private boolean isNewHighscore = false;
    private String gameName = "";

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<User> getTopUsers() {
        return topUsers;
    }

    public void setTopUsers(List<User> topUsers) {
        this.topUsers = topUsers;
    }

    public boolean isNewHighscore() {
        return isNewHighscore;
    }

    public void setNewHighscore(boolean newHighscore) {
        isNewHighscore = newHighscore;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}
