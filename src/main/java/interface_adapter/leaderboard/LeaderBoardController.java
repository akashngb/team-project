package interface_adapter.leaderboard;

import use_case.leaderboard.LeaderBoardInputBoundary;
import use_case.leaderboard.LeaderBoardInputData;

public class LeaderBoardController {

    private final LeaderBoardInputBoundary leaderBoardUseCaseInteractor;

    public LeaderBoardController(LeaderBoardInputBoundary leaderBoardUseCaseInteractor) {
        this.leaderBoardUseCaseInteractor = leaderBoardUseCaseInteractor;
    }

    /**
     * Executes the LeaderBoard Use Case.
     */
    public void execute(String username, Integer score, String gameName) {
        final LeaderBoardInputData leaderBoardInputData = new LeaderBoardInputData(
                username, score, gameName);

        leaderBoardUseCaseInteractor.execute(leaderBoardInputData);
    }
}
