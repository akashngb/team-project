package entity.blockblast;

public class GameState {
    private final Board board;
    private Piece[] currentPieces;
    private int score;
    private boolean gameOver;
    public GameState(Board board, Piece[] currentPieces, int score, boolean gameOver){
        this.board = board;
        this.currentPieces = currentPieces;
        this.score = score;
        this.gameOver = gameOver;
    }
    public Board getBoard() {
        return board;
    }
    public Piece[] getCurrentPieces() {
        return currentPieces;
    }
    public int getScore() {
        return score;
    }
    public boolean isGameOver() {
        return gameOver;
    }
    public void setCurrentPieces(Piece[] currentPieces){
        this.currentPieces = currentPieces;
    }
    public void setScore(int score){
        this.score = score;
    }
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
}
