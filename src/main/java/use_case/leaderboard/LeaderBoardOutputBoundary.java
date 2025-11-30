package use_case.leaderboard;

public interface LeaderBoardOutputBoundary {
    /**
     * Prepares the success view for the Leaderboard Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(LeaderBoardOutputData outputData);

    /**
     * Prepares the failure view for the LeaderBoard Use Case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}
