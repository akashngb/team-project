package use_case.leaderboard;

import entity.User;

import java.util.List;
import java.util.Map;

public interface LeaderBoardDataAccessInterface {
    /**
     *  The DAO for LeaderBoard actions
     * @param user whom we will change the highscore from
     */
    void changeHighscore(String username, String gameName, Integer score);

    /**
     * Checks if the given username exists.
     * @param username the username to look for
     * @return true if a user with the given username exists; false otherwise
     */
    boolean existsByName(String username);

    Map<String, Integer> getHighscoresByName(String username);

    List<User> getTopUsersForGame(String gameName, int limit);
}
