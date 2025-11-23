package view;

import entity.PuzzleMove;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChessBoardPanel extends JPanel {
    private static final int BOARD_SIZE = 8;
    private static final int SQUARE_SIZE = 70;
    private static final Color LIGHT_SQUARE = new Color(240, 217, 181);
    private static final Color DARK_SQUARE = new Color(181, 136, 99);
    private static final Color HIGHLIGHT_COLOR = new Color(255, 255, 0, 100);
    private static final Color MOVE_INDICATOR_COLOR = new Color(130, 200, 130, 180);
    private static final Color LAST_MOVE_COLOR = new Color(155, 199, 0, 100);

    private String[][] board = new String[8][8];
    private Point dragStart = null;
    private String draggedPiece = null;
    private Point selectedSquare = null;
    private List<Point> possibleMoves = new ArrayList<>();
    private MoveListener moveListener;

    // Animation variables
    private Timer animationTimer;
    private String animatingPiece = null;
    private Point animationStart = null;
    private Point animationEnd = null;
    private double animationProgress = 0.0;
    private boolean isAnimating = false;
    private static final int ANIMATION_DURATION_MS = 300;
    private static final int ANIMATION_FPS = 60;

    // Last move highlighting
    private Point lastMoveFrom = null;
    private Point lastMoveTo = null;

    // Pending move (waiting for validation)
    private Point pendingFrom = null;
    private Point pendingTo = null;
    private String pendingPiece = null;
    private String capturedPiece = null;

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
        void onMoveMade(PuzzleMove move, Point from, Point to);
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
        possibleMoves.clear();
        selectedSquare = null;
        lastMoveFrom = null;
        lastMoveTo = null;
        pendingFrom = null;
        pendingTo = null;

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
            private Point currentMousePos = null;

            @Override
            public void mousePressed(MouseEvent e) {
                if (isAnimating) return;

                int col = e.getX() / SQUARE_SIZE;
                int row = e.getY() / SQUARE_SIZE;

                if (row >= 0 && row < 8 && col >= 0 && col < 8) {
                    if (!board[row][col].isEmpty()) {
                        dragStart = new Point(col, row);
                        selectedSquare = new Point(col, row);
                        draggedPiece = board[row][col];
                        currentMousePos = e.getPoint();

                        calculatePossibleMoves(col, row, draggedPiece);
                        repaint();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isAnimating || dragStart == null) {
                    dragStart = null;
                    draggedPiece = null;
                    currentMousePos = null;
                    return;
                }

                int col = e.getX() / SQUARE_SIZE;
                int row = e.getY() / SQUARE_SIZE;

                if (row >= 0 && row < 8 && col >= 0 && col < 8) {
                    Point dragEnd = new Point(col, row);

                    if (!dragStart.equals(dragEnd)) {
                        // Attempt the move
                        attemptMove(dragStart, dragEnd);
                    } else {
                        // Just clicked, clear selection
                        clearSelection();
                    }
                } else {
                    clearSelection();
                }

                dragStart = null;
                draggedPiece = null;
                currentMousePos = null;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragStart != null) {
                    currentMousePos = e.getPoint();
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                currentMousePos = e.getPoint();
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    private void clearSelection() {
        selectedSquare = null;
        possibleMoves.clear();
        repaint();
    }

    private void attemptMove(Point from, Point to) {
        String piece = board[from.y][from.x];
        String captured = board[to.y][to.x];

        // Store pending move info
        pendingFrom = new Point(from.x, from.y);
        pendingTo = new Point(to.x, to.y);
        pendingPiece = piece;
        capturedPiece = captured;

        // Make the move on the board immediately
        board[to.y][to.x] = piece;
        board[from.y][from.x] = "";

        String fromSquare = squareToAlgebraic(from.x, from.y);
        String toSquare = squareToAlgebraic(to.x, to.y);

        PuzzleMove move = new PuzzleMove(fromSquare, toSquare);

        // Animate the move
        animateMove(from, to, piece, () -> {
            // After animation, notify listener
            if (moveListener != null) {
                moveListener.onMoveMade(move, pendingFrom, pendingTo);
            }
        });
    }

    public void onMoveValidated(boolean isCorrect) {
        if (pendingFrom == null || pendingTo == null) {
            return;
        }

        if (isCorrect) {
            // Move was correct, finalize it
            lastMoveFrom = new Point(pendingFrom.x, pendingFrom.y);
            lastMoveTo = new Point(pendingTo.x, pendingTo.y);

            // Clear pending
            pendingFrom = null;
            pendingTo = null;
            pendingPiece = null;
            capturedPiece = null;
            clearSelection();
            repaint();
        } else {
            // Move was incorrect, animate it back
            Point from = new Point(pendingFrom.x, pendingFrom.y);
            Point to = new Point(pendingTo.x, pendingTo.y);

            animateMoveBack(to, from, pendingPiece, () -> {
                // Restore the board state
                board[from.y][from.x] = pendingPiece;
                board[to.y][to.x] = capturedPiece;

                // Clear pending
                pendingFrom = null;
                pendingTo = null;
                pendingPiece = null;
                capturedPiece = null;

                clearSelection();
                repaint();
            });
        }
    }

    private void animateMove(Point from, Point to, String piece, Runnable onComplete) {
        isAnimating = true;
        animatingPiece = piece;
        animationStart = new Point(from.x * SQUARE_SIZE, from.y * SQUARE_SIZE);
        animationEnd = new Point(to.x * SQUARE_SIZE, to.y * SQUARE_SIZE);
        animationProgress = 0.0;

        int delay = 1000 / ANIMATION_FPS;
        double increment = (double) delay / ANIMATION_DURATION_MS;

        animationTimer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animationProgress += increment;

                if (animationProgress >= 1.0) {
                    animationProgress = 1.0;
                    animationTimer.stop();
                    isAnimating = false;
                    animatingPiece = null;

                    if (onComplete != null) {
                        onComplete.run();
                    }
                }

                repaint();
            }
        });

        animationTimer.start();
    }

    private void animateMoveBack(Point from, Point to, String piece, Runnable onComplete) {
        isAnimating = true;
        animatingPiece = piece;
        animationStart = new Point(from.x * SQUARE_SIZE, from.y * SQUARE_SIZE);
        animationEnd = new Point(to.x * SQUARE_SIZE, to.y * SQUARE_SIZE);
        animationProgress = 0.0;

        int delay = 1000 / ANIMATION_FPS;
        double increment = (double) delay / ANIMATION_DURATION_MS;

        animationTimer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animationProgress += increment;

                if (animationProgress >= 1.0) {
                    animationProgress = 1.0;
                    animationTimer.stop();
                    isAnimating = false;
                    animatingPiece = null;

                    if (onComplete != null) {
                        onComplete.run();
                    }
                }

                repaint();
            }
        });

        animationTimer.start();
    }

    public void animateSolution(List<String> moves, Runnable onComplete) {
        if (moves.isEmpty()) {
            if (onComplete != null) onComplete.run();
            return;
        }

        animateSolutionMove(0, moves, onComplete);
    }

    private void animateSolutionMove(int index, List<String> moves, Runnable onComplete) {
        if (index >= moves.size()) {
            if (onComplete != null) onComplete.run();
            return;
        }

        String move = moves.get(index);
        Point from = algebraicToPoint(move.substring(0, 2));
        Point to = algebraicToPoint(move.substring(2, 4));

        String piece = board[from.y][from.x];

        // Make the move on board
        board[to.y][to.x] = piece;
        board[from.y][from.x] = "";

        lastMoveFrom = from;
        lastMoveTo = to;

        // Animate with faster speed
        animateMove(from, to, piece, () -> {
            Timer delayTimer = new Timer(150, e -> {
                ((Timer)e.getSource()).stop();
                animateSolutionMove(index + 1, moves, onComplete);
            });
            delayTimer.setRepeats(false);
            delayTimer.start();
        });
    }

    public Point algebraicToPoint(String algebraic) {
        int col = algebraic.charAt(0) - 'a';
        int row = 8 - Character.getNumericValue(algebraic.charAt(1));
        return new Point(col, row);
    }

    private void calculatePossibleMoves(int col, int row, String piece) {
        possibleMoves.clear();
        String pieceType = piece.toLowerCase();

        switch (pieceType) {
            case "p":
                addPawnMoves(col, row, piece);
                break;
            case "n":
                addKnightMoves(col, row);
                break;
            case "b":
                addBishopMoves(col, row);
                break;
            case "r":
                addRookMoves(col, row);
                break;
            case "q":
                addQueenMoves(col, row);
                break;
            case "k":
                addKingMoves(col, row);
                break;
        }
    }

    private void addPawnMoves(int col, int row, String piece) {
        boolean isWhite = Character.isUpperCase(piece.charAt(0));
        int direction = isWhite ? -1 : 1;

        if (isValidSquare(col, row + direction) && board[row + direction][col].isEmpty()) {
            possibleMoves.add(new Point(col, row + direction));

            int startRow = isWhite ? 6 : 1;
            if (row == startRow && board[row + 2 * direction][col].isEmpty()) {
                possibleMoves.add(new Point(col, row + 2 * direction));
            }
        }

        if (isValidSquare(col - 1, row + direction) && !board[row + direction][col - 1].isEmpty()) {
            possibleMoves.add(new Point(col - 1, row + direction));
        }
        if (isValidSquare(col + 1, row + direction) && !board[row + direction][col + 1].isEmpty()) {
            possibleMoves.add(new Point(col + 1, row + direction));
        }
    }

    private void addKnightMoves(int col, int row) {
        int[][] knightMoves = {{-2,-1}, {-2,1}, {-1,-2}, {-1,2}, {1,-2}, {1,2}, {2,-1}, {2,1}};
        for (int[] move : knightMoves) {
            int newCol = col + move[0];
            int newRow = row + move[1];
            if (isValidSquare(newCol, newRow)) {
                possibleMoves.add(new Point(newCol, newRow));
            }
        }
    }

    private void addBishopMoves(int col, int row) {
        addSlidingMoves(col, row, new int[][]{{1,1}, {1,-1}, {-1,1}, {-1,-1}});
    }

    private void addRookMoves(int col, int row) {
        addSlidingMoves(col, row, new int[][]{{0,1}, {0,-1}, {1,0}, {-1,0}});
    }

    private void addQueenMoves(int col, int row) {
        addBishopMoves(col, row);
        addRookMoves(col, row);
    }

    private void addKingMoves(int col, int row) {
        int[][] kingMoves = {{0,1}, {0,-1}, {1,0}, {-1,0}, {1,1}, {1,-1}, {-1,1}, {-1,-1}};
        for (int[] move : kingMoves) {
            int newCol = col + move[0];
            int newRow = row + move[1];
            if (isValidSquare(newCol, newRow)) {
                possibleMoves.add(new Point(newCol, newRow));
            }
        }
    }

    private void addSlidingMoves(int col, int row, int[][] directions) {
        for (int[] dir : directions) {
            int newCol = col + dir[0];
            int newRow = row + dir[1];
            while (isValidSquare(newCol, newRow)) {
                possibleMoves.add(new Point(newCol, newRow));
                if (!board[newRow][newCol].isEmpty()) {
                    break;
                }
                newCol += dir[0];
                newRow += dir[1];
            }
        }
    }

    private boolean isValidSquare(int col, int row) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
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

        // Draw board squares
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Color squareColor = (row + col) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE;
                g2d.setColor(squareColor);
                g2d.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);

                // Highlight last move
                if (lastMoveFrom != null && lastMoveFrom.x == col && lastMoveFrom.y == row) {
                    g2d.setColor(LAST_MOVE_COLOR);
                    g2d.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                }
                if (lastMoveTo != null && lastMoveTo.x == col && lastMoveTo.y == row) {
                    g2d.setColor(LAST_MOVE_COLOR);
                    g2d.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                }

                // Highlight selected square
                if (selectedSquare != null && selectedSquare.x == col && selectedSquare.y == row) {
                    g2d.setColor(HIGHLIGHT_COLOR);
                    g2d.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                }
            }
        }

        // Draw possible move indicators
        for (Point move : possibleMoves) {
            int centerX = move.x * SQUARE_SIZE + SQUARE_SIZE / 2;
            int centerY = move.y * SQUARE_SIZE + SQUARE_SIZE / 2;

            g2d.setColor(MOVE_INDICATOR_COLOR);

            if (board[move.y][move.x].isEmpty()) {
                int radius = 12;
                g2d.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
            } else {
                g2d.setStroke(new BasicStroke(4));
                g2d.drawOval(move.x * SQUARE_SIZE + 5, move.y * SQUARE_SIZE + 5,
                        SQUARE_SIZE - 10, SQUARE_SIZE - 10);
                g2d.setStroke(new BasicStroke(1));
            }
        }

        // Draw pieces (except the one being dragged)
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (!board[row][col].isEmpty()) {
                    if (dragStart != null && dragStart.x == col && dragStart.y == row && !isAnimating) {
                        continue; // Don't draw piece being dragged
                    }

                    drawPiece(g2d, board[row][col], col * SQUARE_SIZE, row * SQUARE_SIZE);
                }
            }
        }

        // Draw animating piece
        if (isAnimating && animatingPiece != null) {
            int currentX = (int) (animationStart.x + (animationEnd.x - animationStart.x) * animationProgress);
            int currentY = (int) (animationStart.y + (animationEnd.y - animationStart.y) * animationProgress);
            drawPiece(g2d, animatingPiece, currentX, currentY);
        }

        // Draw dragged piece following mouse
        if (draggedPiece != null && !isAnimating && dragStart != null) {
            Point mouse = getMousePosition();
            if (mouse != null) {
                drawPiece(g2d, draggedPiece, mouse.x - SQUARE_SIZE / 2, mouse.y - SQUARE_SIZE / 2);
            }
        }

        // Draw coordinate labels
        drawCoordinates(g2d);
    }

    private void drawPiece(Graphics2D g2d, String piece, int x, int y) {
        String symbol = PIECE_SYMBOLS.get(piece);
        if (symbol != null) {
            g2d.setFont(new Font("Serif", Font.PLAIN, 50));

            // Shadow
            g2d.setColor(new Color(0, 0, 0, 50));
            FontMetrics fm = g2d.getFontMetrics();
            int textX = x + (SQUARE_SIZE - fm.stringWidth(symbol)) / 2;
            int textY = y + ((SQUARE_SIZE - fm.getHeight()) / 2) + fm.getAscent();
            g2d.drawString(symbol, textX + 2, textY + 2);

            // Piece
            g2d.setColor(Color.BLACK);
            g2d.drawString(symbol, textX, textY);
        }
    }

    private void drawCoordinates(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.setColor(new Color(100, 100, 100));

        for (int col = 0; col < 8; col++) {
            char file = (char) ('a' + col);
            g2d.drawString(String.valueOf(file), col * SQUARE_SIZE + 5, getHeight() - 5);
        }

        for (int row = 0; row < 8; row++) {
            int rank = 8 - row;
            g2d.drawString(String.valueOf(rank), getWidth() - 15, row * SQUARE_SIZE + 15);
        }
    }
}
