package view;

import entity.ChessPuzzle;
import entity.PuzzleMove;
import interface_adapter.ViewManagerModel;
import interface_adapter.chess_puzzle.CheckMoveController;
import interface_adapter.chess_puzzle.ChessPuzzleState;
import interface_adapter.chess_puzzle.ChessPuzzleViewModel;
import interface_adapter.chess_puzzle.LoadPuzzlesController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class ChessPuzzleView extends JPanel implements PropertyChangeListener {
    private final String viewName = "chess puzzle";
    private final ChessPuzzleViewModel viewModel;
    private final ViewManagerModel viewManagerModel;

    private ChessBoardPanel boardPanel;
    private JLabel feedbackLabel;
    private JButton nextButton;
    private JButton solutionButton;
    private JLabel ratingLabel;
    private JLabel themesLabel;
    private JTextArea moveHistoryArea;
    private JTextArea evaluationArea;

    private LoadPuzzlesController loadPuzzlesController;
    private CheckMoveController checkMoveController;

    private int currentMoveIndex = 0;
    private List<String> moveHistory = new ArrayList<>();

    public ChessPuzzleView(ChessPuzzleViewModel viewModel, ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
        this.viewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(50, 50, 50));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setOpaque(false);

        // Create board panel
        boardPanel = new ChessBoardPanel();
        boardPanel.setMoveListener((move, from, to) -> handleMove(move, from, to));

        // Create info panel (top)
        JPanel infoPanel = createInfoPanel();

        // Create side panel (right)
        JPanel sidePanel = createSidePanel();

        // Create button panel (bottom)
        JPanel buttonPanel = createButtonPanel();

        // Add components
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(boardPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel("Chess Puzzle Trainer");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ratingLabel = new JLabel("Rating: -");
        ratingLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        ratingLabel.setForeground(new Color(200, 200, 200));
        ratingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        themesLabel = new JLabel("Themes: -");
        themesLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        themesLabel.setForeground(new Color(180, 180, 180));
        themesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        feedbackLabel = new JLabel("Click 'Load Puzzles' to begin");
        feedbackLabel.setFont(new Font("Arial", Font.BOLD, 18));
        feedbackLabel.setForeground(new Color(100, 255, 100));
        feedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(ratingLabel);
        panel.add(themesLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(feedbackLabel);

        return panel;
    }

    private JPanel createSidePanel() {
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(250, 0));
        sidePanel.setBackground(new Color(40, 40, 40));
        sidePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Move History Section
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setOpaque(false);
        TitledBorder historyBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100)),
                "Move History",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                Color.WHITE
        );
        historyPanel.setBorder(historyBorder);

        moveHistoryArea = new JTextArea(10, 20);
        moveHistoryArea.setEditable(false);
        moveHistoryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        moveHistoryArea.setBackground(new Color(30, 30, 30));
        moveHistoryArea.setForeground(new Color(200, 200, 200));
        moveHistoryArea.setText("No moves yet...");

        JScrollPane historyScroll = new JScrollPane(moveHistoryArea);
        historyScroll.setBorder(null);
        historyPanel.add(historyScroll, BorderLayout.CENTER);

        // Evaluation Section
        JPanel evalPanel = new JPanel(new BorderLayout());
        evalPanel.setOpaque(false);
        TitledBorder evalBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100)),
                "Analysis",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                Color.WHITE
        );
        evalPanel.setBorder(evalBorder);

        evaluationArea = new JTextArea(8, 20);
        evaluationArea.setEditable(false);
        evaluationArea.setFont(new Font("Arial", Font.PLAIN, 12));
        evaluationArea.setBackground(new Color(30, 30, 30));
        evaluationArea.setForeground(new Color(200, 200, 200));
        evaluationArea.setLineWrap(true);
        evaluationArea.setWrapStyleWord(true);
        evaluationArea.setText("Make a move to see analysis...");

        JScrollPane evalScroll = new JScrollPane(evaluationArea);
        evalScroll.setBorder(null);
        evalPanel.add(evalScroll, BorderLayout.CENTER);

        // Add panels
        sidePanel.add(historyPanel);
        sidePanel.add(Box.createVerticalStrut(15));
        sidePanel.add(evalPanel);

        return sidePanel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(new Color(60, 60, 60));

        JButton loadButton = new JButton("Load Puzzles");
        styleButton(loadButton, new Color(76, 175, 80));
        loadButton.addActionListener(e -> loadPuzzles());

        JButton resetButton = new JButton("Reset Puzzle");
        styleButton(resetButton, new Color(33, 150, 243));
        resetButton.addActionListener(e -> resetPuzzle());

        solutionButton = new JButton("View Solution");
        styleButton(solutionButton, new Color(255, 152, 0));
        solutionButton.setEnabled(false);
        solutionButton.addActionListener(e -> showSolution());

        nextButton = new JButton("Next Puzzle");
        styleButton(nextButton, new Color(156, 39, 176));
        nextButton.setEnabled(false);
        nextButton.addActionListener(e -> nextPuzzle());

        panel.add(loadButton);
        panel.add(resetButton);
        panel.add(solutionButton);
        panel.add(nextButton);

        return panel;
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(140, 45));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    private void handleMove(PuzzleMove move, Point from, Point to) {
        if (checkMoveController == null) {
            boardPanel.onMoveValidated(false);
            return;
        }

        String moveStr = move.toAlgebraic();
        ChessPuzzleState state = viewModel.getState();
        ChessPuzzle puzzle = state.getCurrentPuzzle();

        if (puzzle == null) {
            boardPanel.onMoveValidated(false);
            return;
        }

        // Check if this is the correct move
        if (currentMoveIndex >= puzzle.getSolutionMoves().size()) {
            boardPanel.onMoveValidated(false);
            feedbackLabel.setText("Puzzle already solved!");
            feedbackLabel.setForeground(new Color(255, 180, 100));
            return;
        }

        String correctMove = puzzle.getSolutionMoves().get(currentMoveIndex);
        boolean isCorrect = moveStr.equals(correctMove);

        System.out.println("Player move: " + moveStr);
        System.out.println("Correct move: " + correctMove);
        System.out.println("Is correct: " + isCorrect);

        // Validate the move on the board
        boardPanel.onMoveValidated(isCorrect);

        if (isCorrect) {
            // Add to move history
            moveHistory.add(moveStr);
            updateMoveHistory();

            // Update the controller
            checkMoveController.execute(moveStr, currentMoveIndex);
            currentMoveIndex++;

            // Update evaluation
            updateEvaluation(moveStr);

            // Update feedback
            feedbackLabel.setText("Correct! Keep going...");
            feedbackLabel.setForeground(new Color(100, 255, 100));

            // Check if puzzle is solved
            if (currentMoveIndex >= puzzle.getSolutionMoves().size()) {
                feedbackLabel.setText("🎉 Puzzle Solved!");
                feedbackLabel.setForeground(new Color(100, 255, 100));
                evaluationArea.setText("Congratulations!\nYou solved the puzzle!");
                nextButton.setEnabled(state.hasNextPuzzle());
            }
        } else {
            // Wrong move - feedback
            feedbackLabel.setText("❌ Incorrect move! Try again.");
            feedbackLabel.setForeground(new Color(255, 100, 100));

            evaluationArea.setText(String.format(
                    "Wrong move!\n\n" +
                            "You played: %s\n" +
                            "Try again...",
                    moveStr
            ));
        }
    }

    private void updateMoveHistory() {
        StringBuilder history = new StringBuilder();
        for (int i = 0; i < moveHistory.size(); i++) {
            int moveNum = (i / 2) + 1;
            if (i % 2 == 0) {
                history.append(moveNum).append(". ");
            }
            history.append(moveHistory.get(i));
            if (i % 2 == 0) {
                history.append(" ");
            } else {
                history.append("\n");
            }
        }
        moveHistoryArea.setText(history.toString());
    }

    private void updateEvaluation(String move) {
        ChessPuzzleState state = viewModel.getState();
        ChessPuzzle puzzle = state.getCurrentPuzzle();

        if (puzzle != null) {
            int totalMoves = puzzle.getSolutionMoves().size();
            int movesLeft = totalMoves - currentMoveIndex;

            String eval = String.format(
                    "Move: %s\n\n" +
                            "Progress: %d/%d moves\n" +
                            "Moves remaining: %d\n\n" +
                            "Status: %s",
                    move,
                    currentMoveIndex,
                    totalMoves,
                    movesLeft,
                    state.getFeedback()
            );

            evaluationArea.setText(eval);
        }
    }

    private void loadPuzzles() {
        if (loadPuzzlesController != null) {
            feedbackLabel.setText("Loading puzzles...");
            feedbackLabel.setForeground(new Color(100, 200, 255));
            loadPuzzlesController.execute(25, 1500);
        }
    }

    private void nextPuzzle() {
        ChessPuzzleState state = viewModel.getState();
        state.nextPuzzle();
        currentMoveIndex = 0;
        moveHistory.clear();

        ChessPuzzle puzzle = state.getCurrentPuzzle();
        if (puzzle != null) {
            loadPuzzleToBoard(puzzle);
            feedbackLabel.setText("Make your move!");
            feedbackLabel.setForeground(new Color(100, 255, 100));
            solutionButton.setEnabled(true);
            nextButton.setEnabled(state.hasNextPuzzle());

            moveHistoryArea.setText("No moves yet...");
            evaluationArea.setText("Make a move to see analysis...");
        }

        viewModel.firePropertyChange();
    }

    private void resetPuzzle() {
        ChessPuzzleState state = viewModel.getState();
        ChessPuzzle puzzle = state.getCurrentPuzzle();

        if (puzzle != null) {
            currentMoveIndex = 0;
            moveHistory.clear();

            boardPanel.loadPosition(puzzle.getFen());

            feedbackLabel.setText("Puzzle reset. Try again!");
            feedbackLabel.setForeground(new Color(100, 200, 255));

            moveHistoryArea.setText("No moves yet...");
            evaluationArea.setText("Make a move to see analysis...");

            solutionButton.setEnabled(true);
        }
    }

    private void showSolution() {
        ChessPuzzleState state = viewModel.getState();
        ChessPuzzle puzzle = state.getCurrentPuzzle();

        if (puzzle != null) {
            // Reset the puzzle first
            currentMoveIndex = 0;
            moveHistory.clear();
            boardPanel.loadPosition(puzzle.getFen());

            feedbackLabel.setText("Playing solution...");
            feedbackLabel.setForeground(new Color(255, 180, 100));

            // Animate the solution
            boardPanel.animateSolution(puzzle.getSolutionMoves(), () -> {
                feedbackLabel.setText("Solution complete!");
                feedbackLabel.setForeground(new Color(100, 255, 100));

                // Update move history with solution
                moveHistory.clear();
                moveHistory.addAll(puzzle.getSolutionMoves());
                updateMoveHistory();

                // Show in evaluation area
                StringBuilder solution = new StringBuilder("Solution played:\n\n");
                for (int i = 0; i < puzzle.getSolutionMoves().size(); i++) {
                    int moveNum = (i / 2) + 1;
                    if (i % 2 == 0) {
                        solution.append(moveNum).append(". ");
                    }
                    solution.append(puzzle.getSolutionMoves().get(i));
                    if (i % 2 == 0) {
                        solution.append(" ");
                    } else {
                        solution.append("\n");
                    }
                }
                evaluationArea.setText(solution.toString());
            });
        }
    }

    private void loadPuzzleToBoard(ChessPuzzle puzzle) {
        boardPanel.loadPosition(puzzle.getFen());
        ratingLabel.setText("Rating: " + puzzle.getRating());
        themesLabel.setText("Themes: " + String.join(", ", puzzle.getThemes()));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        ChessPuzzleState state = viewModel.getState();

        if (state.getErrorMessage() != null) {
            feedbackLabel.setText("Error: " + state.getErrorMessage());
            feedbackLabel.setForeground(new Color(255, 100, 100));
            return;
        }

        ChessPuzzle puzzle = state.getCurrentPuzzle();
        if (puzzle != null) {
            loadPuzzleToBoard(puzzle);
            solutionButton.setEnabled(true);
            nextButton.setEnabled(state.hasNextPuzzle());
        }

        if (evt.getPropertyName().equals("move_result")) {
            feedbackLabel.setText(state.getFeedback());

            if (state.getFeedback().contains("Correct")) {
                feedbackLabel.setForeground(new Color(100, 255, 100));
            } else if (state.getFeedback().contains("Incorrect")) {
                feedbackLabel.setForeground(new Color(255, 100, 100));
            }
        } else if (evt.getPropertyName().equals("puzzle_solved")) {
            feedbackLabel.setText(state.getFeedback());
            feedbackLabel.setForeground(new Color(100, 255, 100));
            evaluationArea.setText("🎉 Puzzle Solved!\n\nGreat job!");
            currentMoveIndex = 0;
            nextButton.setEnabled(state.hasNextPuzzle());
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setLoadPuzzlesController(LoadPuzzlesController controller) {
        this.loadPuzzlesController = controller;
    }

    public void setCheckMoveController(CheckMoveController controller) {
        this.checkMoveController = controller;
    }
}
