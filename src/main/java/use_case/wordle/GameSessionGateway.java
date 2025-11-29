package use_case.wordle;

import entity.wordle.WordleGame;

public interface GameSessionGateway {
    void save(String userId, WordleGame game);
    WordleGame load(String userId);
    void remove(String userId);
    int getScore(String userId);
    void addScore(String userId, int points);
    void setScore(String userId, int score);
}
