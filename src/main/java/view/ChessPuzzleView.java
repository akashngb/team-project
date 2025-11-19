package view;

import entity.ChessPuzzle;
import entity.PuzzleMove;
import interface_adapter.chess_puzzle.CheckMoveController;
import interface_adapter.chess_puzzle.ChessPuzzleState;
import interface_adapter.chess_puzzle.ChessPuzzleViewModel;
import interface_adapter.chess_puzzle.LoadPuzzlesController;
import interface_adapter.ViewManagerModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * View for chess puzzles.
 */
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
    private JLabel moveQualityLabel;

    private LoadPuzzlesController loadPuzzlesController;
    private CheckMoveController checkMoveController;

    private int currentMoveIndex = 0;

    public ChessPuzzleView(ChessPuzzleViewModel viewModel, ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
        this.viewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(48, 46, 43));

        // Create board panel
        boardPanel = new ChessBoardPanel();
        boardPanel.setMoveListener(move -> handleMove(move));

        // Create info panel
        JPanel infoPanel = createInfoPanel();

        // Create button panel
        JPanel buttonPanel = createButtonPanel();

        // Add components
        add(boardPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(48, 46, 43));

        JLabel titleLabel = new JLabel("Chess Puzzle");  // Changed from "Chess Puzzle Trainer"
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ratingLabel = new JLabel("Rating: -");
        ratingLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        ratingLabel.setForeground(Color.LIGHT_GRAY);
        ratingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        themesLabel = new JLabel("Themes: -");
        themesLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        themesLabel.setForeground(Color.LIGHT_GRAY);
        themesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        feedbackLabel = new JLabel("Click 'Load Puzzles' to begin");
        feedbackLabel.setFont(new Font("Arial", Font.BOLD, 16));
        feedbackLabel.setForeground(new Color(129, 182, 76));
        feedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        moveQualityLabel = new JLabel("");
        moveQualityLabel.setFont(new Font("Arial", Font.BOLD, 18));
        moveQualityLabel.setForeground(new Color(255, 170, 0));
        moveQualityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(ratingLabel);
        panel.add(themesLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(feedbackLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(moveQualityLabel);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(new Color(48, 46, 43));

        // Back button
        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(70, 70, 70));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            viewManagerModel.setState("logged in");
            viewManagerModel.firePropertyChange();
        });

        JButton loadButton = new JButton("Load Puzzles");
        loadButton.setFont(new Font("Arial", Font.BOLD, 14));
        loadButton.setBackground(new Color(129, 182, 76));
        loadButton.setForeground(Color.WHITE);
        loadButton.setFocusPainted(false);
        loadButton.addActionListener(e -> loadPuzzles());

        nextButton = new JButton("Next Puzzle");
        nextButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextButton.setBackground(new Color(129, 182, 76));
        nextButton.setForeground(Color.WHITE);
        nextButton.setEnabled(false);
        nextButton.setFocusPainted(false);
        nextButton.addActionListener(e -> nextPuzzle());

        solutionButton = new JButton("View Solution");
        solutionButton.setFont(new Font("Arial", Font.BOLD, 14));
        solutionButton.setBackground(new Color(204, 119, 34));
        solutionButton.setForeground(Color.WHITE);
        solutionButton.setEnabled(false);
        solutionButton.setFocusPainted(false);
        solutionButton.addActionListener(e -> showSolution());

        JButton resetButton = new JButton("Reset Puzzle");
        resetButton.setFont(new Font("Arial", Font.BOLD, 14));
        resetButton.setBackground(new Color(70, 70, 70));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.addActionListener(e -> resetPuzzle());

        panel.add(backButton);
        panel.add(loadButton);
        panel.add(resetButton);
        panel.add(solutionButton);
        panel.add(nextButton);

        return panel;
    }

    private void handleMove(PuzzleMove move) {
        if (checkMoveController != null) {
            String moveStr = move.toAlgebraic();
            checkMoveController.execute(moveStr, currentMoveIndex);
            currentMoveIndex++;
        }
    }

    private void loadPuzzles() {
        if (loadPuzzlesController != null) {
            feedbackLabel.setText("Loading puzzles...");
            feedbackLabel.setForeground(new Color(129, 182, 76));
            moveQualityLabel.setText("");
            loadPuzzlesController.execute(25, 1500);
        }
    }

    private void nextPuzzle() {
        ChessPuzzleState state = viewModel.getState();
        state.nextPuzzle();
        currentMoveIndex = 0;

        ChessPuzzle puzzle = state.getCurrentPuzzle();
        if (puzzle != null) {
            loadPuzzleToBoard(puzzle);
            feedbackLabel.setText("Find the best move!");
            feedbackLabel.setForeground(new Color(129, 182, 76));
            moveQualityLabel.setText("");
            solutionButton.setEnabled(true);
            nextButton.setEnabled(state.hasNextPuzzle());
        }

        viewModel.firePropertyChange();
    }

    private void resetPuzzle() {
        ChessPuzzleState state = viewModel.getState();
        ChessPuzzle puzzle = state.getCurrentPuzzle();

        if (puzzle != null) {
            currentMoveIndex = 0;
            loadPuzzleToBoard(puzzle);
            feedbackLabel.setText("Puzzle reset. Try again!");
            feedbackLabel.setForeground(new Color(129, 182, 76));
            moveQualityLabel.setText("");
        }
    }

    private void showSolution() {
        ChessPuzzleState state = viewModel.getState();
        ChessPuzzle puzzle = state.getCurrentPuzzle();

        if (puzzle != null) {
            StringBuilder solution = new StringBuilder("<html><body style='padding:10px'>");
            solution.append("<h2>Solution</h2>");
            solution.append("<p style='font-size:14px'>The best moves are:</p>");
            solution.append("<ol style='font-size:14px'>");

            for (int i = 0; i < puzzle.getSolutionMoves().size(); i++) {
                String move = puzzle.getSolutionMoves().get(i);
                solution.append("<li>").append(formatMove(move)).append("</li>");
            }

            solution.append("</ol></body></html>");

            JOptionPane.showMessageDialog(this,
                    new JLabel(solution.toString()),
                    "Puzzle Solution",
                    JOptionPane.INFORMATION_MESSAGE);

            feedbackLabel.setText("Solution displayed");
            feedbackLabel.setForeground(new Color(204, 119, 34));
        }
    }

    private String formatMove(String move) {
        // Convert UCI notation (e2e4) to readable format (e2-e4)
        if (move.length() >= 4) {
            return move.substring(0, 2) + " → " + move.substring(2, 4);
        }
        return move;
    }

    private void loadPuzzleToBoard(ChessPuzzle puzzle) {
        boardPanel.loadPosition(puzzle.getFen());
        ratingLabel.setText("Rating: " + puzzle.getRating());

        String themes = String.join(", ", puzzle.getThemes());
        if (themes.length() > 50) {
            themes = themes.substring(0, 47) + "...";
        }
        themesLabel.setText("Themes: " + themes);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        ChessPuzzleState state = viewModel.getState();

        if (state.getErrorMessage() != null) {
            feedbackLabel.setText("Error: " + state.getErrorMessage());
            feedbackLabel.setForeground(Color.RED);
            moveQualityLabel.setText("");
            JOptionPane.showMessageDialog(this,
                    state.getErrorMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
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
                feedbackLabel.setForeground(new Color(129, 182, 76));
                moveQualityLabel.setText("✓ Excellent!");
                moveQualityLabel.setForeground(new Color(129, 182, 76));
            } else if (state.getFeedback().contains("Incorrect")) {
                feedbackLabel.setForeground(new Color(220, 50, 50));
                moveQualityLabel.setText("✗ Try again");
                moveQualityLabel.setForeground(new Color(220, 50, 50));
                currentMoveIndex = Math.max(0, currentMoveIndex - 1);
            }
        } else if (evt.getPropertyName().equals("puzzle_solved")) {
            feedbackLabel.setText("Puzzle Solved! 🎉");
            feedbackLabel.setForeground(new Color(255, 215, 0));
            moveQualityLabel.setText("★★★ Perfect!");
            moveQualityLabel.setForeground(new Color(255, 215, 0));

            JOptionPane.showMessageDialog(this,
                    "Congratulations! You solved the puzzle!\n\nRating: " +
                            state.getCurrentPuzzle().getRating(),
                    "Puzzle Solved!",
                    JOptionPane.INFORMATION_MESSAGE);

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