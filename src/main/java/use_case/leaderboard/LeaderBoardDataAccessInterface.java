package use_case.leaderboard;

import entity.User;

import java.util.List;

public interface LeaderBoardDataAccessInterface {
    /**
     *  The DAO for LeaderBoard actions
     * @param user whom we will change the highscore from
     */
    void changeHighscore(User user);

    List<User> getTopUsersForGame(String gameName, int limit);
}
