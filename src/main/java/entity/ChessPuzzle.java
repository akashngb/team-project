package entity;

import java.util.List;

/**
 * Represents a chess puzzle with its position and solution moves.
 */
public class ChessPuzzle {
    private final String puzzleId;
    private final String fen;  // Chess positions in FEN notation (standard notation to describe chess piece in a board)
    private final List<String> solutionMoves; // solution moves are in algebraic notations which is why its a List of Strings
    private final int rating;
    private final List<String> themes;

    public ChessPuzzle(String puzzleId, String fen, List<String> solutionMoves,
                       int rating, List<String> themes) {
        this.puzzleId = puzzleId;
        this.fen = fen;
        this.solutionMoves = solutionMoves;
        this.rating = rating;
        this.themes = themes;
    }

    public String getPuzzleId() {
        return puzzleId;
    }

    public String getFen() {
        return fen;
    }

    public List<String> getSolutionMoves() {
        return solutionMoves;
    }

    public int getRating() {
        return rating;
    }

    public List<String> getThemes() {
        return themes;
    }
}
