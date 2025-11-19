package interface_adapter.blockblast;

import use_case.blockblast.PlacePieceInputBoundary;
import use_case.blockblast.PlacePieceRequestModel;

public class BlockBlastController {
    private final PlacePieceInputBoundary placePieceUseCase;
    public BlockBlastController(PlacePieceInputBoundary placePieceUseCase) {
        this.placePieceUseCase = placePieceUseCase;
    }
    public void placePiece(int pieceIndex, int row, int col){
        PlacePieceRequestModel pieceRequestModel = new PlacePieceRequestModel(pieceIndex, row, col);
        placePieceUseCase.execute(pieceRequestModel);
    }
}
