package entity.blockblast;

public class Board {
    private final int rows;
    private final int cols;
    private final PieceColor[][] grid;

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new PieceColor[rows][cols];
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }

    public boolean isFilled(int r, int c) {
        return grid[r][c] != null;
    }

    public PieceColor[][] getGrid() {
        return grid;
    }

    public boolean canPlace(Piece piece, int baseRow, int baseCol) {
        for (Position cell : piece.getCells()) {
            int r = baseRow + cell.row;
            int c = baseCol + cell.col;
            if (r < 0 || r >= rows || c < 0 || c >= cols) {
                return false;
            }
            if (grid[r][c] != null) {
                return false;
            }
        }
        return true;
    }

    public void place(Piece piece, int baseRow, int baseCol) {
        for (Position cell : piece.getCells()) {
            int r = baseRow + cell.row;
            int c = baseCol + cell.col;
            grid[r][c] = piece.getColor();
        }
    }

    public int clearFullLines() {
        int cleared = 0;

        for (int r = 0; r < rows; r++) {
            boolean full = true;
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == null) {
                    full = false;
                    break;
                }
            }
            if (full) {
                for (int c = 0; c < cols; c++) {
                    grid[r][c] = null;
                }
                cleared++;
            }
        }

        for (int c = 0; c < cols; c++) {
            boolean full = true;
            for (int r = 0; r < rows; r++) {
                if (grid[r][c] == null) {
                    full = false;
                    break;
                }
            }
            if (full) {
                for (int r = 0; r < rows; r++) {
                    grid[r][c] = null;
                }
                cleared++;
            }
        }

        return cleared;
    }

    public void clear() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = null;
            }
        }
    }
}
