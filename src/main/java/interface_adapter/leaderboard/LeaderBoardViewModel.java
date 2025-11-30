package interface_adapter.leaderboard;

import interface_adapter.ViewModel;
import interface_adapter.login.LoginState;

public class LeaderBoardViewModel extends ViewModel<LeaderBoardState> {
    public LeaderBoardViewModel() {
        super("leader board");
        setState(new LeaderBoardState());
    }
}
