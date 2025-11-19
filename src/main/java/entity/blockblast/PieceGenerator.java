package entity.blockblast;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class PieceGenerator {
    private final Random random = new Random();
    public Piece generateRandomPiece(){
        int type = random.nextInt(5);
        List<Position> cells = new ArrayList<>();

        switch (type) {
            case 0:
                cells.add(new Position(0, 0));
                break;
            case 1:
                cells.add(new Position(0, 0));
                cells.add(new Position(0, 1));
                break;
            case 2:
                cells.add(new Position(0, 0));
                cells.add(new Position(1, 0));
                break;
            case 3:
                cells.add(new Position(0, 0));
                cells.add(new Position(0, 1));
                cells.add(new Position(1, 0));
                cells.add(new Position(1, 1));
                break;
            case 4:
                cells.add(new Position(0, 0));
                cells.add(new Position(1, 0));
                cells.add(new Position(1, 1));
                break;
        }

        return new Piece(cells);
    }
}
