package app.blockblast;

import entity.blockblast.*;
import interface_adapter.ViewManagerModel;
import interface_adapter.blockblast.BlockBlastController;
import interface_adapter.blockblast.BlockBlastPresenter;
import interface_adapter.blockblast.BlockBlastViewModel;
import interface_adapter.leaderboard.LeaderBoardController;
import use_case.blockblast.PlacePieceInputBoundary;
import use_case.blockblast.PlacePieceInteractor;
import view.ImagePanel;
import view.blockblast.BlockBlastView;

import javax.swing.*;
import java.awt.*;

public class BlockBlastAppBuilder {

    public static JFrame buildFrame() {
        return buildFrame(null, null, null);
    }

    public static JFrame buildFrame(ViewManagerModel viewManagerModel,
                                   LeaderBoardController leaderBoardController,
                                   String userId) {
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

        BlockBlastView view = new BlockBlastView(viewModel, controller, viewManagerModel);

        // Wire up leaderboard if available
        if (leaderBoardController != null) {
            view.setLeaderBoardController(leaderBoardController);
        }

        // Set user ID if available
        if (userId != null && !userId.isEmpty()) {
            view.setUserId(userId);
        }

        ImagePanel background = new ImagePanel("/images/blockblast_bg.png");
        background.setLayout(new BorderLayout());
        view.setOpaque(false);
        background.add(view, BorderLayout.CENTER);

        boolean[][] initialBoard = new boolean[board.getRows()][board.getCols()];
        PieceColor[][] initialColors = board.getGrid();
        viewModel.setState(
                initialBoard,
                initialColors,
                gameState.getScore(),
                gameState.isGameOver(),
                "",
                gameState.getCurrentPieces()
        );

        JFrame frame = new JFrame("Block Blast");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(background);

        frame.setSize(1300, 700);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        return frame;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = buildFrame();
            frame.setVisible(true);
        });
    }
}
