package entity.blockblast;

public class Board {
    private final int rows;
    private final int cols;
    private boolean[][] grid;
    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new boolean[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = false;
            }
        }
    }
    public boolean isFilled(int r, int c){
        return grid[r][c];
    }
    public int getRows() {
        return rows;
    }
    public int getCols() {
        return cols;
    }

    public boolean canPlace(Piece piece, int baseRow, int baseCol){
        for (Position cell : piece.getCells()) {
            int r = baseRow + cell.row;
            int c = baseCol + cell.col;
            if (grid[r][c]){
                    return false;
            }
        }
        return true;
    }

    public void place(Piece piece, int baseRow, int baseCol){
        for (Position cell : piece.getCells()) {
            int r = baseRow + cell.row;
            int c = baseCol + cell.col;
            grid[r][c] = true;
        }
    }

    public int clearFullLines(){

        boolean[] fullRow = new boolean[rows];
        boolean[] fullCol = new boolean[cols];

        for (int r = 0; r < rows; r++) {
            boolean full = true;
            for (int c = 0; c < cols; c++) {
                if (!grid[r][c]) {
                    full = false;
                    break;
                }
            }
            fullRow[r] = full;
        }

        for (int c = 0; c < cols; c++) {
            boolean full = true;
            for (int r = 0; r < rows; r++) {
                if (!grid[r][c]) {
                    full = false;
                    break;
                }
            }
            fullCol[c] = full;
        }

        int cleared = 0;

        for (int r = 0; r < rows; r++) {
            if (fullRow[r]) {
                cleared++;
                for (int c = 0; c < cols; c++) {
                    grid[r][c] = false;
                }
            }
        }

        for (int c = 0; c < cols; c++) {
            if (fullCol[c]) {
                cleared++;
                for (int r = 0; r < rows; r++) {
                    grid[r][c] = false;
                }
            }
        }
        return cleared;
    }
}
