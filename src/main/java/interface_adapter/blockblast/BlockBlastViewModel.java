package interface_adapter.blockblast;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class BlockBlastViewModel {
    public static final String BLOCKBLAST_PROPERTY = "blockblast";
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private boolean[][] board;
    private int score;
    private boolean gameOver;
    private String message;
    public void setState(boolean[][] board, int score, boolean gameOver, String message){
        this.board = board;
        this.score = score;
        this.gameOver = gameOver;
        this.message = message;
        support.firePropertyChange(BLOCKBLAST_PROPERTY, null, null);
    }
    public boolean[][] getBoard() {
        return board;
    }
    public int getScore() {
        return score;
    }
    public boolean isGameOver() {
        return gameOver;
    }
    public String getMessage() {
        return message;
    }
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }
}

