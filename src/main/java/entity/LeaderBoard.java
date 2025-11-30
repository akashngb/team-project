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
     * Creates a new LeaderBoard with the given params. A specific User is associated
     * to its highscores
     * @param Users the list of users
     * @param Highscores their highscores
     * @throws IllegalArgumentException if any params are empty
     */
    public LeaderBoard(ArrayList<Map<String, Integer>> Highscores, ArrayList<User> Users){
        leaderboard = new HashMap<>();
        for (int i = 0; i < Users.size(); i++) {
            User user = Users.get(i);
            Map<String, Integer> highscores = Highscores.get(i);
            leaderboard.put(user, highscores);
        };
    }

    public Map<User, Map<String, Integer>> getLeaderboard() { return leaderboard; }

    public ArrayList<User> getUsersWithLessScoreInGame(Integer score, String game) {
        try {
            Games.valueOf(game);
            ArrayList<User> users = new ArrayList<>();
            for (Map.Entry<User, Map<String, Integer>> entry : leaderboard.entrySet())  {
                if (entry.getValue().get(game) < score) {
                    users.add(entry.getKey());
                }
            }
            return users;
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No such game");
        }
    }

    public Integer getBestScore() {
        Integer bestScore = 0;
        for (Map.Entry<Integer, User> entry : leaderboard.entrySet()) {
            if (entry.getKey() > bestScore) {bestScore = entry.getKey();}
        }
        return bestScore;
    }

    public Integer getHighScore(User user) {
        for (Map.Entry<Integer, User> entry : leaderboard.entrySet()) {
            if (entry.getValue().equals(user)) { return entry.getKey();}
        }
        throw new IllegalArgumentException("No such user");
    }
}
