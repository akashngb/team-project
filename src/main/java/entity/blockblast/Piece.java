package entity.blockblast;

import java.util.List;

public class Piece {

    private final List<Position> cells;

    public Piece(List<Position> cells){
        this.cells = cells;
    }

    public List<Position> getCells() {
        return cells;
    }
}
