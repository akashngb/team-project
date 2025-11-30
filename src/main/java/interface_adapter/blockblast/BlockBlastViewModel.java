package interface_adapter.blockblast;

import entity.blockblast.Piece;
import entity.blockblast.PieceColor;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class BlockBlastViewModel {
    public static final String BLOCKBLAST_PROPERTY = "blockblast";
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private boolean[][] board;
    private PieceColor[][] cellColors;
    private int score;
    private boolean gameOver;
    private String message;
    private Piece[] pieces;

    public void setState(boolean[][] board,PieceColor[][] cellColors, int score, boolean gameOver, String message, Piece[] pieces) {
        this.board = board;
        this.cellColors = cellColors;
        this.score = score;
        this.gameOver = gameOver;
        this.message = message;
        this.pieces = pieces;
        support.firePropertyChange(BLOCKBLAST_PROPERTY, null, null);
    }

    public boolean[][] getBoard() { return board; }

    public PieceColor[][] getCellColors() { return cellColors; }

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

    public Piece[] getPieces() {
        return pieces;
    }
}

