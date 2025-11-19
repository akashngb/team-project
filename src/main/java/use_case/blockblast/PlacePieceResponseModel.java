package use_case.blockblast;

import entity.blockblast.GameState;

public class PlacePieceResponseModel {
    private final GameState gameState;
    public PlacePieceResponseModel(GameState gameState) {
        this.gameState = gameState;
    }
    public GameState getGameState() {
        return gameState;
    }
}
