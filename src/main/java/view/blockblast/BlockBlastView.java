package view.blockblast;

import entity.blockblast.Piece;
import interface_adapter.blockblast.BlockBlastController;
import interface_adapter.blockblast.BlockBlastViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

public class BlockBlastView extends JPanel implements PropertyChangeListener{
    private final BlockBlastViewModel viewModel;
    private final BlockBlastController controller;
    private final int CELL_SIZE = 30;
    private final JLabel scoreLabel;
    private final JLabel messageLabel;
    private static final int BOARD_ROWS  = 10;
    private static final int BOARD_COLS  = 10;
    private int selectedPieceIndex = 0;
    private final JPanel previewPanel;

    public BlockBlastView(BlockBlastViewModel viewModel, BlockBlastController controller) {
        this.viewModel = viewModel;
        this.controller = controller;
        this.viewModel.addPropertyChangeListener(this);

        this.setLayout(new BorderLayout());
        int width = BOARD_ROWS * CELL_SIZE;
        int height = BOARD_COLS * CELL_SIZE;
        this.setPreferredSize(new Dimension(width, height));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        scoreLabel   = new JLabel("Score: 0");
        messageLabel = new JLabel("");
        topBar.add(scoreLabel);
        topBar.add(messageLabel);
        this.add(topBar, BorderLayout.NORTH);

        previewPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawPreviews(g);
            }
        };
        previewPanel.setPreferredSize(new Dimension(width, 80));
        this.add(previewPanel, BorderLayout.SOUTH);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleBoardClick(e);
            }
        });

        previewPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handlePreviewClick(e);
            }
        });
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        boolean [][] board = viewModel.getBoard();
        if (board == null) {
            return;
        }
        for (int r = 0; r<BOARD_ROWS; r++ ) {
            for (int c = 0; c<BOARD_COLS; c++ ) {
                int x =  r*CELL_SIZE;
                int y =  c*CELL_SIZE;
                g.drawRect(x,y,CELL_SIZE,CELL_SIZE);
                if (board[r][c]){
                    g.setColor(Color.BLUE);
                    g.fillRect(x+1,y+1,CELL_SIZE-2,CELL_SIZE-2);
                }
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        scoreLabel.setText("Score: " + viewModel.getScore());
        messageLabel.setText(viewModel.getMessage());
        repaint();
        previewPanel.repaint();
    }

    private void handleBoardClick(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }
        int x = e.getX();
        int y = e.getY();
        if (y < 0) {
            return;
        }
        int col = x / CELL_SIZE;
        int row = y / CELL_SIZE;
        if (row >= BOARD_ROWS || col < 0 || col >= BOARD_COLS) {
            return;
        }
        controller.placePiece(selectedPieceIndex, row, col);
    }

    public void handlePreviewClick(MouseEvent e) {
        int x = e.getX();
        int previewSize = 4 * 15;
        int margin = 10;

        for (int i = 0; i < 3; i++) {
            int baseX = margin + i * (previewSize + margin);
            int baseY = 10;
            int w = previewSize;
            int h = previewSize;

            if (x >= baseX && x <= baseX + w &&
                    e.getY() >= baseY && e.getY() <= baseY + h) {
                selectedPieceIndex = i;
                previewPanel.repaint();
                break;
            }
        }
    }

    public void drawPreviews(Graphics g) {
        Piece[] pieces = viewModel.getPieces();
        return;

    }
}
