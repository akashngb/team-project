package view.blockblast;

import entity.blockblast.Piece;
import entity.blockblast.Position;
import interface_adapter.blockblast.BlockBlastController;
import interface_adapter.blockblast.BlockBlastViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BlockBlastView extends JPanel implements PropertyChangeListener {

    private static final int CELL_SIZE      = 30;
    private static final int BOARD_ROWS     = 8;
    private static final int BOARD_COLS     = 8;
    private static final int OFFSET_Y       = 40;
    private static final int PREVIEW_HEIGHT = 80;

    private final BlockBlastViewModel viewModel;
    private final BlockBlastController controller;

    private final JLabel scoreLabel;
    private final JLabel messageLabel;

    private int selectedPieceIndex = 0;
    private final JPanel previewPanel;

    public BlockBlastView(BlockBlastViewModel viewModel, BlockBlastController controller) {
        this.viewModel = viewModel;
        this.controller = controller;
        this.viewModel.addPropertyChangeListener(this);

        this.setLayout(new BorderLayout());
        int width  = BOARD_COLS * CELL_SIZE;
        int height = OFFSET_Y + BOARD_ROWS * CELL_SIZE + PREVIEW_HEIGHT;
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
        previewPanel.setPreferredSize(new Dimension(width, PREVIEW_HEIGHT));
        this.add(previewPanel, BorderLayout.SOUTH);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleBoardClick(e);
            }
        });

        // 预览区域的点击
        previewPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handlePreviewClick(e);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        boolean[][] board = viewModel.getBoard();
        if (board == null) {
            return;
        }

        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLS; c++) {
                int x = c * CELL_SIZE;                 // ⭐ 列 → x
                int y = OFFSET_Y + r * CELL_SIZE;      // ⭐ 行 → y + 偏移

                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(x, y, CELL_SIZE, CELL_SIZE);

                if (board[r][c]) {
                    g.setColor(Color.BLUE);
                    g.fillRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);
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

    /** 点击棋盘 */
    private void handleBoardClick(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }

        int x = e.getX();
        int y = e.getY();

        if (y < OFFSET_Y) {
            return;
        }

        int col = x / CELL_SIZE;
        int row = (y - OFFSET_Y) / CELL_SIZE;

        if (row < 0 || row >= BOARD_ROWS || col < 0 || col >= BOARD_COLS) {
            return;
        }

        controller.placePiece(selectedPieceIndex, row, col);
    }

    private void handlePreviewClick(MouseEvent e) {
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

    private void drawPreviews(Graphics g) {
        Piece[] pieces = viewModel.getPieces();
        if (pieces == null) {
            return;
        }

        int cellSize    = 15;
        int previewSize = 4 * cellSize;
        int margin      = 10;

        for (int i = 0; i < pieces.length; i++) {
            Piece p = pieces[i];
            if (p == null) continue;

            int baseX = margin + i * (previewSize + margin);
            int baseY = 10;

            if (i == selectedPieceIndex) {
                g.setColor(Color.DARK_GRAY);
                g.drawRect(baseX - 3, baseY - 3, previewSize + 6, previewSize + 6);
            }

            g.setColor(Color.BLACK);
            for (Position pos : p.getCells()) {
                int x = baseX + pos.col * cellSize;
                int y = baseY + pos.row * cellSize;
                g.drawRect(x, y, cellSize, cellSize);
                g.fillRect(x + 1, y + 1, cellSize - 2, cellSize - 2);
            }
        }
    }
}
