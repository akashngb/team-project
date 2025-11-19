package view;

import entity.ChessPuzzle;
import entity.PuzzleMove;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Panel that displays a draggable chess board.
 */
public class ChessBoardPanel extends JPanel {
    private static final int BOARD_SIZE = 8;
    private static final int SQUARE_SIZE = 70;
    private static final Color LIGHT_SQUARE = new Color(240, 217, 181);
    private static final Color DARK_SQUARE = new Color(181, 136, 99);
    private static final Color HIGHLIGHT_COLOR = new Color(255, 255, 0, 100);

    private String[][] board = new String[8][8];
    private Point dragStart = null;
    private Point dragEnd = null;
    private String draggedPiece = null;
    private MoveListener moveListener;

    // Unicode chess pieces
    private static final Map<String, String> PIECE_SYMBOLS = new HashMap<>();
    static {
        PIECE_SYMBOLS.put("K", "♔");
        PIECE_SYMBOLS.put("Q", "♕");
        PIECE_SYMBOLS.put("R", "♖");
        PIECE_SYMBOLS.put("B", "♗");
        PIECE_SYMBOLS.put("N", "♘");
        PIECE_SYMBOLS.put("P", "♙");
        PIECE_SYMBOLS.put("k", "♚");
        PIECE_SYMBOLS.put("q", "♛");
        PIECE_SYMBOLS.put("r", "♜");
        PIECE_SYMBOLS.put("b", "♝");
        PIECE_SYMBOLS.put("n", "♞");
        PIECE_SYMBOLS.put("p", "♟");
    }

    public interface MoveListener {
        void onMoveMade(PuzzleMove move);
    }

    public ChessBoardPanel() {
        setPreferredSize(new Dimension(SQUARE_SIZE * 8, SQUARE_SIZE * 8));
        initializeBoard();
        setupMouseListeners();
    }

    private void initializeBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = "";
            }
        }
    }

    public void setMoveListener(MoveListener listener) {
        this.moveListener = listener;
    }

    public void loadPosition(String fen) {
        initializeBoard();

        String[] parts = fen.split(" ");
        String position = parts[0];

        String[] ranks = position.split("/");
        for (int rank = 0; rank < ranks.length && rank < 8; rank++) {
            int file = 0;
            for (char c : ranks[rank].toCharArray()) {
                if (Character.isDigit(c)) {
                    file += Character.getNumericValue(c);
                } else {
                    board[rank][file] = String.valueOf(c);
                    file++;
                }
            }
        }

        repaint();
    }

    private void setupMouseListeners() {
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int col = e.getX() / SQUARE_SIZE;
                int row = e.getY() / SQUARE_SIZE;

                if (row >= 0 && row < 8 && col >= 0 && col < 8) {
                    if (!board[row][col].isEmpty()) {
                        dragStart = new Point(col, row);
                        draggedPiece = board[row][col];
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (dragStart != null) {
                    int col = e.getX() / SQUARE_SIZE;
                    int row = e.getY() / SQUARE_SIZE;

                    if (row >= 0 && row < 8 && col >= 0 && col < 8) {
                        dragEnd = new Point(col, row);

                        if (!dragStart.equals(dragEnd)) {
                            makeMove(dragStart, dragEnd);
                        }
                    }
                }

                dragStart = null;
                dragEnd = null;
                draggedPiece = null;
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                repaint();
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    private void makeMove(Point from, Point to) {
        String piece = board[from.y][from.x];
        board[to.y][to.x] = piece;
        board[from.y][from.x] = "";

        String fromSquare = squareToAlgebraic(from.x, from.y);
        String toSquare = squareToAlgebraic(to.x, to.y);

        PuzzleMove move = new PuzzleMove(fromSquare, toSquare);

        if (moveListener != null) {
            moveListener.onMoveMade(move);
        }

        repaint();
    }

    private String squareToAlgebraic(int col, int row) {
        char file = (char) ('a' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw board
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Color squareColor = (row + col) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE;
                g2d.setColor(squareColor);
                g2d.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);

                // Highlight dragged square
                if (dragStart != null && dragStart.x == col && dragStart.y == row) {
                    g2d.setColor(HIGHLIGHT_COLOR);
                    g2d.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                }

                // Draw pieces
                if (!board[row][col].isEmpty() &&
                        !(dragStart != null && dragStart.x == col && dragStart.y == row)) {
                    drawPiece(g2d, board[row][col], col * SQUARE_SIZE, row * SQUARE_SIZE);
                }
            }
        }

        // Draw dragged piece
        if (draggedPiece != null && dragStart != null) {
            Point mouse = getMousePosition();
            if (mouse != null) {
                drawPiece(g2d, draggedPiece, mouse.x - SQUARE_SIZE/2, mouse.y - SQUARE_SIZE/2);
            }
        }
    }

    private void drawPiece(Graphics2D g2d, String piece, int x, int y) {
        String symbol = PIECE_SYMBOLS.get(piece);
        if (symbol != null) {
            g2d.setFont(new Font("Serif", Font.PLAIN, 50));
            g2d.setColor(Color.BLACK);

            FontMetrics fm = g2d.getFontMetrics();
            int textX = x + (SQUARE_SIZE - fm.stringWidth(symbol)) / 2;
            int textY = y + ((SQUARE_SIZE - fm.getHeight()) / 2) + fm.getAscent();

            g2d.drawString(symbol, textX, textY);
        }
    }
}