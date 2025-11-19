package interface_adapter.chess_puzzle;

import entity.ChessPuzzle;
import java.util.ArrayList;
import java.util.List;

/**
 * State for the chess puzzle view.
 */
public class ChessPuzzleState {
    private List<ChessPuzzle> puzzles = new ArrayList<>();
    private int currentPuzzleIndex = 0;
    private String feedback = "";
    private boolean showSolution = false;
    private String errorMessage = null;

    public ChessPuzzleState() {
    }

    public ChessPuzzleState(ChessPuzzleState copy) {
        this.puzzles = new ArrayList<>(copy.puzzles);
        this.currentPuzzleIndex = copy.currentPuzzleIndex;
        this.feedback = copy.feedback;
        this.showSolution = copy.showSolution;
        this.errorMessage = copy.errorMessage;
    }

    public List<ChessPuzzle> getPuzzles() {
        return puzzles;
    }

    public void setPuzzles(List<ChessPuzzle> puzzles) {
        this.puzzles = puzzles;
    }

    public ChessPuzzle getCurrentPuzzle() {
        if (puzzles.isEmpty() || currentPuzzleIndex >= puzzles.size()) {
            return null;
        }
        return puzzles.get(currentPuzzleIndex);
    }

    public int getCurrentPuzzleIndex() {
        return currentPuzzleIndex;
    }

    public void setCurrentPuzzleIndex(int index) {
        this.currentPuzzleIndex = index;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public boolean isShowSolution() {
        return showSolution;
    }

    public void setShowSolution(boolean showSolution) {
        this.showSolution = showSolution;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void nextPuzzle() {
        if (currentPuzzleIndex < puzzles.size() - 1) {
            currentPuzzleIndex++;
            feedback = "";
            showSolution = false;
        }
    }

    public boolean hasNextPuzzle() {
        return currentPuzzleIndex < puzzles.size() - 1;
    }
}