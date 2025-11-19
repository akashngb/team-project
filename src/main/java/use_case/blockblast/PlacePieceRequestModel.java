package use_case.blockblast;

public class PlacePieceRequestModel {
    private final int pieceIndex;
    private final int row;
    private final int col;
    public PlacePieceRequestModel(int pieceIndex, int row, int col) {
        this.pieceIndex = pieceIndex;
        this.row = row;
        this.col = col;
    }
    public int getPieceIndex() {
        return pieceIndex;
    }
    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }
}
