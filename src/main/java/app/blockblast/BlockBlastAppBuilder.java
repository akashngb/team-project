package app.blockblast;

import entity.blockblast.Board;
import entity.blockblast.GameState;
import entity.blockblast.Piece;
import entity.blockblast.PieceGenerator;
import interface_adapter.blockblast.BlockBlastController;
import interface_adapter.blockblast.BlockBlastPresenter;
import interface_adapter.blockblast.BlockBlastViewModel;
import use_case.blockblast.PlacePieceInputBoundary;
import use_case.blockblast.PlacePieceInteractor;
import view.blockblast.BlockBlastView;

import javax.swing.*;

public class BlockBlastAppBuilder {

    public static JFrame buildFrame() {
        Board board = new Board(8, 8);
        PieceGenerator generator = new PieceGenerator();

        Piece[] pieces = new Piece[3];
        for (int i = 0; i < pieces.length; i++) {
            pieces[i] = generator.generateRandomPiece();
        }

        GameState gameState = new GameState(board, pieces, 0, false);

        BlockBlastViewModel viewModel = new BlockBlastViewModel();
        BlockBlastPresenter presenter = new BlockBlastPresenter(viewModel);
        PlacePieceInputBoundary interactor =
                new PlacePieceInteractor(gameState, generator, presenter);
        BlockBlastController controller =
                new BlockBlastController(interactor);

        BlockBlastView view = new BlockBlastView(viewModel, controller);

        boolean[][] initialBoard = new boolean[board.getRows()][board.getCols()];
        viewModel.setState(initialBoard,
                gameState.getScore(),
                gameState.isGameOver(),
                "",
                gameState.getCurrentPieces());

        JFrame frame = new JFrame("Block Blast");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(view);
        frame.pack();
        frame.setLocationRelativeTo(null);

        return frame;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> buildFrame().setVisible(true));
    }
}
