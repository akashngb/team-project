package use_case.chess_puzzle;

/**
 * Input data for loading puzzles use case.
 */
public class LoadPuzzlesInputData {
    private final int count;
    private final int rating;

    public LoadPuzzlesInputData(int count, int rating) {
        this.count = count;
        this.rating = rating;
    }

    public int getCount() {
        return count;
    }

    public int getRating() {
        return rating;
    }
}