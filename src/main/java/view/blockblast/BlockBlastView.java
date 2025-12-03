package view.blockblast;

import entity.Games;
import entity.blockblast.Piece;
import entity.blockblast.PieceColor;
import interface_adapter.ViewManagerModel;
import interface_adapter.blockblast.BlockBlastController;
import interface_adapter.blockblast.BlockBlastViewModel;
import interface_adapter.leaderboard.LeaderBoardController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class BlockBlastView extends JPanel implements PropertyChangeListener {

    private final BlockBlastViewModel viewModel;
    private final BlockBlastController controller;
    private ViewManagerModel viewManagerModel;
    private LeaderBoardController leaderBoardController;
    private String userId = "default-user"; // Will be set from logged-in user
    private boolean scoreSubmitted = false; // Track if score was already submitted for current game

    private static final int CELL_SIZE = 50;
    private static final int OFFSET_Y  = 100;

    private static final int PREVIEW_CELL_SIZE = 26;
    private static final int PREVIEW_BOX_SIZE  = PREVIEW_CELL_SIZE * 4;
    private static final int PREVIEW_MARGIN    = 20;
    private static final Color GRID_COLOR = new Color(220, 230, 255, 160);

    private final JLabel scoreLabel;
    private final JLabel messageLabel;
    private final JButton newGameButton;


    private final JPanel previewPanel;
    private int selectedPieceIndex = -1;

    public BlockBlastView(BlockBlastViewModel viewModel,
                          BlockBlastController controller,
                          ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.controller = controller;
        this.viewManagerModel = viewManagerModel;
        this.viewModel.addPropertyChangeListener(this);
        this.setLayout(new BorderLayout());
        this.setOpaque(false);

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setOpaque(false);
        scoreLabel   = new JLabel("Score: 0");
        messageLabel = new JLabel("");
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(scoreLabel.getFont().deriveFont(Font.BOLD, 24f));
        messageLabel.setForeground(Color.YELLOW);
        messageLabel.setFont(messageLabel.getFont().deriveFont(Font.BOLD, 18f));

        newGameButton = new JButton("New Game");
        newGameButton.setFocusPainted(false);
        newGameButton.setBackground(new Color(255, 255, 255, 220));
        newGameButton.setForeground(Color.BLACK);
        newGameButton.setFont(newGameButton.getFont().deriveFont(Font.BOLD, 16f));
        newGameButton.addActionListener(e -> {
            selectedPieceIndex = -1; // 选中状态也一起清空
            resetScoreSubmission(); // Reset score submission flag for new game
            controller.newGame();
        });

        JButton backButton = new JButton("Back");
        backButton.setFocusPainted(false);
        backButton.setBackground(new Color(128, 128, 128, 220));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(backButton.getFont().deriveFont(Font.BOLD, 16f));
        backButton.addActionListener(e -> {
            java.awt.Window window = SwingUtilities.getWindowAncestor(BlockBlastView.this);
            if (window != null) {
                window.dispose();
            }
            if (viewManagerModel != null) {
                viewManagerModel.setState("logged in");
                viewManagerModel.firePropertyChange();
            }
        });

        JButton leaderboardButton = new JButton("Leaderboard");
        leaderboardButton.setFocusPainted(false);
        leaderboardButton.setBackground(new Color(255, 215, 0, 220)); // Gold color
        leaderboardButton.setForeground(Color.BLACK);
        leaderboardButton.setFont(leaderboardButton.getFont().deriveFont(Font.BOLD, 16f));
        leaderboardButton.addActionListener(e -> {
            if (viewManagerModel != null) {
                // Switch to leaderboard view and refresh its data
                submitScoreToLeaderboard(0);
                viewManagerModel.setState("leaderboard");
                viewManagerModel.firePropertyChange();
            }
        });

        topBar.add(scoreLabel);
        topBar.add(Box.createHorizontalStrut(30));
        topBar.add(newGameButton);
        topBar.add(Box.createHorizontalStrut(30));
        topBar.add(backButton);
        topBar.add(Box.createHorizontalStrut(10));
        topBar.add(leaderboardButton);
        topBar.add(Box.createHorizontalStrut(30));
        topBar.add(messageLabel);
        add(topBar, BorderLayout.NORTH);

        previewPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawPreviews(g);
            }
        };
        previewPanel.setOpaque(false);
        previewPanel.setPreferredSize(new Dimension(400, PREVIEW_BOX_SIZE + 2 * PREVIEW_MARGIN));
        add(previewPanel, BorderLayout.SOUTH);

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

    private int getBoardOffsetX(boolean[][] board) {
        int rows = board.length;
        int cols = board[0].length;
        int boardWidth = cols * CELL_SIZE;
        int panelWidth = getWidth();
        return Math.max(0, (panelWidth - boardWidth) / 2);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        boolean[][] board = viewModel.getBoard();
        PieceColor[][] colors = viewModel.getCellColors();
        if (board == null) {
            return;
        }

        int rows = board.length;
        int cols = board[0].length;
        int offsetX = getBoardOffsetX(board);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = offsetX + c * CELL_SIZE;
                int y = OFFSET_Y + r * CELL_SIZE;

                g.setColor(GRID_COLOR);
                g.drawRect(x, y, CELL_SIZE, CELL_SIZE);

                if (board[r][c]) {
                    Color fill = toAwtColor(colors[r][c]);
                    g.setColor(fill);
                    g.fillRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);

                    g.setColor(Color.WHITE);
                    g.drawRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);
                }
            }
        }

    }

    private Color toAwtColor(PieceColor pc) {
        if (pc == null) return Color.BLUE;
        switch (pc) {
            case RED:    return Color.RED;
            case GREEN:  return Color.GREEN;
            case BLUE:   return Color.BLUE;
            case YELLOW: return Color.YELLOW;
            case ORANGE: return Color.ORANGE;
            case PURPLE: return new Color(128, 0, 128);
            case CYAN:   return Color.CYAN;
            default:     return Color.BLUE;
        }
    }

    private void handleBoardClick(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }

        if (selectedPieceIndex < 0) {
            return;
        }

        boolean[][] board = viewModel.getBoard();
        if (board == null) {
            return;
        }

        int rows = board.length;
        int cols = board[0].length;

        int x = e.getX();
        int y = e.getY();

        if (y < OFFSET_Y) {
            return;
        }

        int offsetX = getBoardOffsetX(board);
        int boardWidth  = cols * CELL_SIZE;
        int boardHeight = rows * CELL_SIZE;

        if (x < offsetX || x >= offsetX + boardWidth) {
            return;
        }
        if (y < OFFSET_Y || y >= OFFSET_Y + boardHeight) {
            return;
        }

        int col = (x - offsetX) / CELL_SIZE;
        int row = (y - OFFSET_Y) / CELL_SIZE;

        controller.placePiece(selectedPieceIndex, row, col);
    }

    private int getPreviewOffsetX() {
        int totalWidth = 3 * PREVIEW_BOX_SIZE + 4 * PREVIEW_MARGIN;
        int panelWidth = previewPanel.getWidth();
        return Math.max(0, (panelWidth - totalWidth) / 2);
    }

    private void drawPreviews(Graphics g) {
        Piece[] pieces = viewModel.getPieces();
        if (pieces == null) return;

        int offsetX = getPreviewOffsetX();
        int baseY   = 10;

        for (int i = 0; i < 3; i++) {
            int boxX = offsetX + PREVIEW_MARGIN + i * (PREVIEW_BOX_SIZE + PREVIEW_MARGIN);
            int boxY = baseY;

            g.setColor(Color.WHITE);
            g.fillRect(boxX, boxY, PREVIEW_BOX_SIZE, PREVIEW_BOX_SIZE);

            if (i == selectedPieceIndex) {
                g.setColor(Color.BLACK);
                ((Graphics2D) g).setStroke(new BasicStroke(2f));
            } else {
                g.setColor(Color.LIGHT_GRAY);
                ((Graphics2D) g).setStroke(new BasicStroke(1f));
            }
            g.drawRect(boxX, boxY, PREVIEW_BOX_SIZE, PREVIEW_BOX_SIZE);

            Piece p = pieces[i];
            if (p == null) continue;

            int minRow = Integer.MAX_VALUE, maxRow = Integer.MIN_VALUE;
            int minCol = Integer.MAX_VALUE, maxCol = Integer.MIN_VALUE;
            for (var cell : p.getCells()) {
                minRow = Math.min(minRow, cell.row);
                maxRow = Math.max(maxRow, cell.row);
                minCol = Math.min(minCol, cell.col);
                maxCol = Math.max(maxCol, cell.col);
            }
            int pieceRows = maxRow - minRow + 1;
            int pieceCols = maxCol - minCol + 1;

            int piecePixelW = pieceCols * PREVIEW_CELL_SIZE;
            int piecePixelH = pieceRows * PREVIEW_CELL_SIZE;

            int startX = boxX + (PREVIEW_BOX_SIZE - piecePixelW) / 2;
            int startY = boxY + (PREVIEW_BOX_SIZE - piecePixelH) / 2;

            for (var cell : p.getCells()) {
                int relRow = cell.row - minRow;
                int relCol = cell.col - minCol;

                int cellX = startX + relCol * PREVIEW_CELL_SIZE;
                int cellY = startY + relRow * PREVIEW_CELL_SIZE;

                g.setColor(toAwtColor(p.getColor()));
                g.fillRect(cellX + 1, cellY + 1,
                        PREVIEW_CELL_SIZE - 2, PREVIEW_CELL_SIZE - 2);
            }
        }
    }

    private void handlePreviewClick(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        int offsetX = getPreviewOffsetX();
        int baseY   = 5;

        for (int i = 0; i < 3; i++) {
            int boxX = offsetX + PREVIEW_MARGIN + i * (PREVIEW_BOX_SIZE + PREVIEW_MARGIN);
            int boxY = baseY;
            int w    = PREVIEW_BOX_SIZE;
            int h    = PREVIEW_BOX_SIZE;

            if (x >= boxX && x <= boxX + w &&
                    y >= boxY && y <= boxY + h) {
                selectedPieceIndex = i;
                previewPanel.repaint();
                break;
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        scoreLabel.setText("Score: " + viewModel.getScore());
        messageLabel.setText(viewModel.getMessage());

        // Check if game is over and submit score to leaderboard
        if (viewModel.isGameOver() && !scoreSubmitted) {
            submitScoreToLeaderboard(viewModel.getScore());
            scoreSubmitted = true; // Prevent multiple submissions
        }

        repaint();
        previewPanel.repaint();
    }

    private void submitScoreToLeaderboard(int finalScore) {
        if (leaderBoardController != null && userId != null) {
            // Submit to leaderboard - it will check if it's a new highscore
            leaderBoardController.execute(userId, finalScore, String.valueOf(Games.BLOCKBLAST));
        }
    }

    public void setLeaderBoardController(LeaderBoardController leaderBoardController) {
        this.leaderBoardController = leaderBoardController;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Reset score submission flag when starting a new game
    private void resetScoreSubmission() {
        scoreSubmitted = false;
    }
}
