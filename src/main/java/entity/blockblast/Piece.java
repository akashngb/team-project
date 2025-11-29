package entity.blockblast;

import java.util.List;

public class Piece {

    private final List<Position> cells;
    private final PieceColor color;

    public Piece(List<Position> cells, PieceColor color) {
        this.cells = cells;
        this.color = color;
    }

    public List<Position> getCells() {
        return cells;
    }
    public PieceColor getColor() {
        return color;
    }
}
