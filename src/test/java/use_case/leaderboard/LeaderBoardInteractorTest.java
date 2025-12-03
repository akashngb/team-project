package use_case.leaderboard;

import entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for LeaderBoard use case.
 * Tests the LeaderBoardInteractor with various scenarios using mock DAO and Presenter.
 */
class LeaderBoardInteractorTest {

    /**
     * Mock implementation of LeaderBoardDataAccessInterface for testing.
     * Stores users in memory and simulates database behavior.
     */
    private static class InMemoryLeaderBoardDAO implements LeaderBoardDataAccessInterface {
        private final Map<String, User> users = new HashMap<>();

        public void addUser(User user) {
            users.put(user.getName(), user);
        }

        @Override
        public boolean existsByName(String username) {
            return users.containsKey(username);
        }

        @Override
        public Map<String, Integer> getHighscoresByName(String username) {
            User user = users.get(username);
            return user != null ? new HashMap<>(user.getHighscores()) : new HashMap<>();
        }

        @Override
        public void changeHighscore(String username, String gameName, Integer score) {
            User user = users.get(username);
            if (user != null) {
                Map<String, Integer> highscores = new HashMap<>(user.getHighscores());
                highscores.put(gameName, score);
                // Create new user with updated highscores
                User updatedUser = new User(user.getName(), user.getPassword(), highscores);
                users.put(username, updatedUser);
            }
        }

        @Override
        public List<User> getTopUsersForGame(String gameName, int limit) {
            List<User> usersWithScores = new ArrayList<>();

            // Filter users who have played this game
            for (User user : users.values()) {
                if (user.getHighscores().containsKey(gameName)) {
                    usersWithScores.add(user);
                }
            }

            // Sort by score descending
            usersWithScores.sort((u1, u2) -> {
                Integer score1 = u1.getHighscores().get(gameName);
                Integer score2 = u2.getHighscores().get(gameName);
                return Integer.compare(score2, score1);
            });

            // Return top N users
            return usersWithScores.subList(0, Math.min(limit, usersWithScores.size()));
        }
    }

    /**
     * Mock presenter to capture output data for assertions.
     */
    private static class TestLeaderBoardPresenter implements LeaderBoardOutputBoundary {
        private LeaderBoardOutputData lastOutputData;
        private String lastError;
        private boolean successCalled = false;
        private boolean failCalled = false;

        @Override
        public void prepareSuccessView(LeaderBoardOutputData outputData) {
            this.lastOutputData = outputData;
            this.successCalled = true;
            this.failCalled = false;
        }

        @Override
        public void prepareFailView(String error) {
            this.lastError = error;
            this.failCalled = true;
            this.successCalled = false;
        }

        public LeaderBoardOutputData getLastOutputData() {
            return lastOutputData;
        }

        public String getLastError() {
            return lastError;
        }

        public boolean wasSuccessCalled() {
            return successCalled;
        }

        public boolean wasFailCalled() {
            return failCalled;
        }

        public void reset() {
            lastOutputData = null;
            lastError = null;
            successCalled = false;
            failCalled = false;
        }
    }

    private InMemoryLeaderBoardDAO dao;
    private TestLeaderBoardPresenter presenter;
    private LeaderBoardInteractor interactor;

    @BeforeEach
    void setUp() {
        dao = new InMemoryLeaderBoardDAO();
        presenter = new TestLeaderBoardPresenter();
        interactor = new LeaderBoardInteractor(dao, presenter);

        // Set up test users with initial scores
        Map<String, Integer> aliceScores = new HashMap<>();
        aliceScores.put("WORDLE", 50);
        aliceScores.put("BLOCKBLAST", 100);
        dao.addUser(new User("Alice", "password123", aliceScores));

        Map<String, Integer> bobScores = new HashMap<>();
        bobScores.put("WORDLE", 75);
        bobScores.put("BLOCKBLAST", 80);
        dao.addUser(new User("Bob", "password456", bobScores));

        Map<String, Integer> charlieScores = new HashMap<>();
        charlieScores.put("WORDLE", 60);
        dao.addUser(new User("Charlie", "password789", charlieScores));

        Map<String, Integer> dianaScores = new HashMap<>();
        dianaScores.put("BLOCKBLAST", 120);
        dao.addUser(new User("Diana", "passwordABC", dianaScores));
    }

    @Test
    void testNewHighscore_Success() {
        // Alice submits a new highscore for WORDLE (better than her previous 50)
        LeaderBoardInputData inputData = new LeaderBoardInputData("Alice", 90, "WORDLE");

        interactor.execute(inputData);

        // Verify success view was called
        assertTrue(presenter.wasSuccessCalled(), "Success view should be called");
        assertFalse(presenter.wasFailCalled(), "Fail view should not be called");

        // Verify output data
        LeaderBoardOutputData outputData = presenter.getLastOutputData();
        assertNotNull(outputData, "Output data should not be null");
        assertTrue(outputData.isNewHighscore(), "Should be marked as new highscore");
        assertTrue(outputData.isSuccess(), "Should be successful");
        assertEquals("WORDLE", outputData.getGameName(), "Game name should match");

        // Verify leaderboard contains users
        List<User> topUsers = outputData.getTopUsers();
        assertNotNull(topUsers, "Top users list should not be null");
        assertFalse(topUsers.isEmpty(), "Top users list should not be empty");

        // Verify Alice's score was updated in DAO
        Map<String, Integer> aliceScores = dao.getHighscoresByName("Alice");
        assertEquals(90, aliceScores.get("WORDLE"), "Alice's WORDLE score should be updated to 90");

        // Verify leaderboard order (Alice: 90, Bob: 75, Charlie: 60)
        assertEquals("Alice", topUsers.get(0).getName(), "Alice should be #1 with score 90");
        assertEquals("Bob", topUsers.get(1).getName(), "Bob should be #2 with score 75");
        assertEquals("Charlie", topUsers.get(2).getName(), "Charlie should be #3 with score 60");
    }

    @Test
    void testNotANewHighscore_Success() {
        // Alice submits a score lower than her previous highscore (50)
        LeaderBoardInputData inputData = new LeaderBoardInputData("Alice", 30, "WORDLE");

        interactor.execute(inputData);

        // Verify success view was called
        assertTrue(presenter.wasSuccessCalled(), "Success view should be called");
        assertFalse(presenter.wasFailCalled(), "Fail view should not be called");

        // Verify output data
        LeaderBoardOutputData outputData = presenter.getLastOutputData();
        assertNotNull(outputData, "Output data should not be null");
        assertFalse(outputData.isNewHighscore(), "Should NOT be marked as new highscore");
        assertTrue(outputData.isSuccess(), "Should still be successful");
        assertEquals("WORDLE", outputData.getGameName(), "Game name should match");

        // Verify Alice's score was NOT updated
        Map<String, Integer> aliceScores = dao.getHighscoresByName("Alice");
        assertEquals(50, aliceScores.get("WORDLE"), "Alice's WORDLE score should remain 50");

        // Verify leaderboard is still returned
        List<User> topUsers = outputData.getTopUsers();
        assertNotNull(topUsers, "Top users list should not be null");
        assertEquals(3, topUsers.size(), "Should have 3 WORDLE players");
    }

    @Test
    void testFirstTimePlayingGame_Success() {
        // Diana plays WORDLE for the first time (she only has BLOCKBLAST score)
        LeaderBoardInputData inputData = new LeaderBoardInputData("Diana", 85, "WORDLE");

        interactor.execute(inputData);

        // Verify success view was called
        assertTrue(presenter.wasSuccessCalled(), "Success view should be called");

        // Verify output data
        LeaderBoardOutputData outputData = presenter.getLastOutputData();
        assertTrue(outputData.isNewHighscore(), "First score should be a new highscore");

        // Verify Diana's score was added
        Map<String, Integer> dianaScores = dao.getHighscoresByName("Diana");
        assertEquals(85, dianaScores.get("WORDLE"), "Diana's WORDLE score should be 85");

        // Verify Diana appears in leaderboard
        List<User> topUsers = outputData.getTopUsers();
        boolean dianaInLeaderboard = topUsers.stream()
                .anyMatch(user -> user.getName().equals("Diana"));
        assertTrue(dianaInLeaderboard, "Diana should appear in WORDLE leaderboard");
    }

    @Test
    void testUserDoesNotExist_Failure() {
        // Try to submit score for non-existent user
        LeaderBoardInputData inputData = new LeaderBoardInputData("NonExistentUser", 100, "WORDLE");

        interactor.execute(inputData);

        // Verify fail view was called
        assertTrue(presenter.wasFailCalled(), "Fail view should be called");
        assertFalse(presenter.wasSuccessCalled(), "Success view should not be called");

        // Verify error message
        String error = presenter.getLastError();
        assertNotNull(error, "Error message should not be null");
        assertTrue(error.contains("NonExistentUser"), "Error should mention the username");
        assertTrue(error.contains("Account does not exist"), "Error should explain the problem");
    }

    @Test
    void testLeaderboardOrdering() {
        // Add more users to test ordering
        Map<String, Integer> eveScores = new HashMap<>();
        eveScores.put("WORDLE", 95);
        dao.addUser(new User("Eve", "passwordEve", eveScores));

        Map<String, Integer> frankScores = new HashMap<>();
        frankScores.put("WORDLE", 40);
        dao.addUser(new User("Frank", "passwordFrank", frankScores));

        // Submit a score to trigger leaderboard fetch
        LeaderBoardInputData inputData = new LeaderBoardInputData("Alice", 30, "WORDLE");
        interactor.execute(inputData);

        // Verify leaderboard order (descending by score)
        List<User> topUsers = presenter.getLastOutputData().getTopUsers();
        assertEquals(5, topUsers.size(), "Should have 5 WORDLE players");

        // Check order: Eve(95), Bob(75), Charlie(60), Alice(50), Frank(40)
        assertEquals("Eve", topUsers.get(0).getName());
        assertEquals(95, topUsers.get(0).getHighscores().get("WORDLE"));

        assertEquals("Bob", topUsers.get(1).getName());
        assertEquals(75, topUsers.get(1).getHighscores().get("WORDLE"));

        assertEquals("Charlie", topUsers.get(2).getName());
        assertEquals(60, topUsers.get(2).getHighscores().get("WORDLE"));

        assertEquals("Alice", topUsers.get(3).getName());
        assertEquals(50, topUsers.get(3).getHighscores().get("WORDLE"));

        assertEquals("Frank", topUsers.get(4).getName());
        assertEquals(40, topUsers.get(4).getHighscores().get("WORDLE"));
    }

    @Test
    void testDifferentGames_SeparateLeaderboards() {
        // Submit score for WORDLE
        LeaderBoardInputData wordleInput = new LeaderBoardInputData("Alice", 30, "WORDLE");
        interactor.execute(wordleInput);
        List<User> wordleLeaderboard = presenter.getLastOutputData().getTopUsers();

        presenter.reset();

        // Submit score for BLOCKBLAST
        LeaderBoardInputData blockBlastInput = new LeaderBoardInputData("Alice", 90, "BLOCKBLAST");
        interactor.execute(blockBlastInput);
        List<User> blockBlastLeaderboard = presenter.getLastOutputData().getTopUsers();

        // Verify different games have different leaderboards
        assertEquals(3, wordleLeaderboard.size(), "WORDLE should have 3 players");
        assertEquals(3, blockBlastLeaderboard.size(), "BLOCKBLAST should have 3 players");

        // Verify they contain different users
        boolean wordleHasCharlie = wordleLeaderboard.stream()
                .anyMatch(user -> user.getName().equals("Charlie"));
        boolean blockBlastHasCharlie = blockBlastLeaderboard.stream()
                .anyMatch(user -> user.getName().equals("Charlie"));

        assertTrue(wordleHasCharlie, "Charlie should be in WORDLE leaderboard");
        assertFalse(blockBlastHasCharlie, "Charlie should NOT be in BLOCKBLAST leaderboard");
    }

    @Test
    void testEqualScores_BothInLeaderboard() {
        // Add user with same score as Bob
        Map<String, Integer> georgeScores = new HashMap<>();
        georgeScores.put("WORDLE", 75); // Same as Bob
        dao.addUser(new User("George", "passwordGeo", georgeScores));

        LeaderBoardInputData inputData = new LeaderBoardInputData("Alice", 30, "WORDLE");
        interactor.execute(inputData);

        List<User> topUsers = presenter.getLastOutputData().getTopUsers();

        // Both Bob and George should be in leaderboard
        boolean hasBob = topUsers.stream().anyMatch(u -> u.getName().equals("Bob"));
        boolean hasGeorge = topUsers.stream().anyMatch(u -> u.getName().equals("George"));

        assertTrue(hasBob, "Bob should be in leaderboard");
        assertTrue(hasGeorge, "George should be in leaderboard");
    }

    @Test
    void testScoreOfZero_IsNotNewHighscore() {
        // Alice has score of 50, submit 0
        LeaderBoardInputData inputData = new LeaderBoardInputData("Alice", 0, "WORDLE");

        interactor.execute(inputData);

        assertFalse(presenter.getLastOutputData().isNewHighscore(),
                "Score of 0 should not be a new highscore when previous score exists");
        assertEquals(50, dao.getHighscoresByName("Alice").get("WORDLE"),
                "Score should not be updated");
    }

    @Test
    void testGameNameInOutputData() {
        LeaderBoardInputData inputData = new LeaderBoardInputData("Alice", 100, "BLOCKBLAST");

        interactor.execute(inputData);

        LeaderBoardOutputData outputData = presenter.getLastOutputData();
        assertEquals("BLOCKBLAST", outputData.getGameName(),
                "Game name should be passed through to output data");
    }

    @Test
    void testMultipleSubmissions_OnlyHighestKept() {
        // Submit multiple scores for Alice in WORDLE
        interactor.execute(new LeaderBoardInputData("Alice", 60, "WORDLE")); // Higher than 50
        assertEquals(60, dao.getHighscoresByName("Alice").get("WORDLE"));

        interactor.execute(new LeaderBoardInputData("Alice", 40, "WORDLE")); // Lower, should not update
        assertEquals(60, dao.getHighscoresByName("Alice").get("WORDLE"));

        interactor.execute(new LeaderBoardInputData("Alice", 80, "WORDLE")); // Higher, should update
        assertEquals(80, dao.getHighscoresByName("Alice").get("WORDLE"));
    }
}
