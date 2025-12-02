package interface_adapter.wordle;

import entity.User;

public class WordleState {
    private User user;

    public void setUser(User user){
        this.user = user;
    }

    public User getUser(){
        return user;
    }
}
