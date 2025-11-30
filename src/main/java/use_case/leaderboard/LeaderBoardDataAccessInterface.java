package use_case.leaderboard;

import entity.User;

public interface LeaderBoardDataAccessInterface {
    /**
     *  The DAO for LeaderBoard actions
     * @param user whom we will change the highscore from
     */
    void changeHighscore(User user);
}
