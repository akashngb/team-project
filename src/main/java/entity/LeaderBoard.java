package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Entity representing a LeaderBoard containing Users
 */

public class LeaderBoard {

    private final ArrayList<User> leaderboard;

    /**
     * Creates a new LeaderBoard with the given params.
     * @param Users the list of users
     * @throws IllegalArgumentException if any params are empty
     */
    public LeaderBoard(ArrayList<User> Users){
        if (Users == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        this.leaderboard = Users;
    }

    public ArrayList<User> getLeaderboard() { return leaderboard; }
}
