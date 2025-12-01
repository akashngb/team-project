package interface_adapter.leaderboard;

import interface_adapter.ViewManagerModel;
import use_case.leaderboard.LeaderBoardOutputBoundary;
import use_case.leaderboard.LeaderBoardOutputData;

public class LeaderBoardPresenter implements LeaderBoardOutputBoundary {

    private final LeaderBoardViewModel leaderBoardViewModel;
    private final ViewManagerModel viewManagerModel;

    public LeaderBoardPresenter(ViewManagerModel viewManagerModel,
                                LeaderBoardViewModel leaderBoardViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.leaderBoardViewModel = leaderBoardViewModel;
    }

    @Override
    public void prepareSuccessView(LeaderBoardOutputData response) {
        final LeaderBoardState leaderBoardState = leaderBoardViewModel.getState();
        leaderBoardState.setTopUsers(response.getTopUsers());
        leaderBoardState.setNewHighscore(response.isNewHighscore());
        leaderBoardState.setGameName(response.getGameName());
        leaderBoardState.setErrorMessage("");
        leaderBoardViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String error) {
        final LeaderBoardState leaderBoardState = leaderBoardViewModel.getState();
        leaderBoardState.setErrorMessage(error);
        leaderBoardViewModel.firePropertyChange();
    }
}
