package view;

import entity.User;
import interface_adapter.ViewManagerModel;
import interface_adapter.leaderboard.LeaderBoardController;
import interface_adapter.leaderboard.LeaderBoardState;
import interface_adapter.leaderboard.LeaderBoardViewModel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * The View for displaying the leaderboard after a game.
 */
public class LeaderBoardView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "leaderboard";
    private final LeaderBoardViewModel leaderBoardViewModel;
    private final ViewManagerModel viewManagerModel;
    private LeaderBoardController leaderBoardController;

    private final JLabel titleLabel;
    private final JLabel newHighscoreLabel;
    private final JTable leaderboardTable;
    private final DefaultTableModel tableModel;
    private final JButton backButton;
    private final JLabel errorLabel;

    public LeaderBoardView(LeaderBoardViewModel leaderBoardViewModel, ViewManagerModel viewManagerModel) {
        this.leaderBoardViewModel = leaderBoardViewModel;
        this.viewManagerModel = viewManagerModel;
        this.leaderBoardViewModel.addPropertyChangeListener(this);

        // Font and Color Configuration
        final Font titleFont = FontLoader.jersey10.deriveFont(Font.PLAIN, 80f);
        final Font headerFont = FontLoader.jersey10.deriveFont(Font.PLAIN, 40f);
        final Font tableFont = FontLoader.jersey10.deriveFont(Font.PLAIN, 30f);
        final Font buttonFont = FontLoader.jersey10.deriveFont(Font.PLAIN, 40f);
        final Color customGray = new Color(0xD9D9D9);
        final Color highlightColor = new Color(255, 215, 0); // Gold color for new highscore
        final Border buttonPadding = BorderFactory.createEmptyBorder(3, 20, 3, 20);

        // Title Label
        titleLabel = new JLabel("Leaderboard", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setOpaque(false);

        // New Highscore Label
        newHighscoreLabel = new JLabel("🎉 NEW HIGH SCORE! 🎉", SwingConstants.CENTER);
        newHighscoreLabel.setFont(headerFont);
        newHighscoreLabel.setForeground(highlightColor);
        newHighscoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        newHighscoreLabel.setOpaque(false);
        newHighscoreLabel.setVisible(false); // Hidden by default

        // Error Label
        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setFont(FontLoader.jersey10.deriveFont(Font.PLAIN, 30f));
        errorLabel.setForeground(Color.RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setOpaque(false);

        // Table Model and Table Setup
        String[] columnNames = {"Rank", "Username", "Score"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        leaderboardTable = new JTable(tableModel);
        leaderboardTable.setFont(tableFont);
        leaderboardTable.setRowHeight(40);
        leaderboardTable.setBackground(new Color(255, 255, 255, 200)); // Semi-transparent white
        leaderboardTable.setForeground(Color.BLACK);
        leaderboardTable.setSelectionBackground(customGray);
        leaderboardTable.setSelectionForeground(Color.BLACK);
        leaderboardTable.setShowGrid(true);
        leaderboardTable.setGridColor(Color.LIGHT_GRAY);

        // Center align all cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < leaderboardTable.getColumnCount(); i++) {
            leaderboardTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Style the table header
        JTableHeader tableHeader = leaderboardTable.getTableHeader();
        tableHeader.setFont(headerFont);
        tableHeader.setBackground(customGray);
        tableHeader.setForeground(Color.BLACK);
        tableHeader.setReorderingAllowed(false);

        // Set column widths
        leaderboardTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Rank
        leaderboardTable.getColumnModel().getColumn(1).setPreferredWidth(300); // Username
        leaderboardTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Score

        // Scroll Pane for Table
        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        scrollPane.setPreferredSize(new Dimension(600, 450));
        scrollPane.setMaximumSize(new Dimension(600, 450));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(customGray, 2));

        // Back Button
        backButton = new JButton("Back to Menu");
        backButton.setFont(buttonFont);
        backButton.setForeground(Color.BLACK);
        backButton.setBackground(customGray);
        backButton.setBorder(buttonPadding);
        backButton.setBorderPainted(true);
        backButton.setFocusPainted(false);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(this);

        // Content Panel Setup
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(newHighscoreLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(errorLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(scrollPane);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(backButton);
        contentPanel.add(Box.createVerticalGlue());

        // Main Layout with Background
        this.setLayout(new BorderLayout());

        // Create a semi-transparent background panel
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                // Create a gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(20, 30, 48),
                    0, getHeight(), new Color(36, 59, 85)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.add(contentPanel, BorderLayout.CENTER);

        this.add(backgroundPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(backButton)) {
            // Navigate back to logged in view
            viewManagerModel.setState("logged in");
            viewManagerModel.firePropertyChange();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final LeaderBoardState state = (LeaderBoardState) evt.getNewValue();
        updateView(state);
    }

    /**
     * Updates the view based on the current state.
     * @param state the current LeaderBoardState
     */
    private void updateView(LeaderBoardState state) {
        // Update error message
        String errorMessage = state.getErrorMessage();
        if (errorMessage != null && !errorMessage.isEmpty()) {
            errorLabel.setText(errorMessage);
            errorLabel.setVisible(true);
        } else {
            errorLabel.setText("");
            errorLabel.setVisible(false);
        }

        // Update new highscore indicator
        newHighscoreLabel.setVisible(state.isNewHighscore());

        // Update game name in title if available
        String gameName = state.getGameName();
        if (gameName != null && !gameName.isEmpty()) {
            titleLabel.setText(gameName + " Leaderboard");
            // Update table with scores for the specific game
            updateTableForGame(state.getTopUsers(), gameName);
        } else {
            titleLabel.setText("Leaderboard");
            // Update table with generic scores
            updateTable(state.getTopUsers());
        }
    }

    /**
     * Updates the leaderboard table with the list of top users.
     * @param topUsers the list of top users to display
     */
    private void updateTable(List<User> topUsers) {
        // Clear existing rows
        tableModel.setRowCount(0);

        if (topUsers == null || topUsers.isEmpty()) {
            // Show "No data" message
            return;
        }

        // Add rows for each user
        for (int i = 0; i < topUsers.size(); i++) {
            User user = topUsers.get(i);
            int rank = i + 1;
            String username = user.getName();

            // Get the score for the current game
            // Note: We'll need to know which game to look up the score for
            int score = user.getHighscores().values().stream()
                    .max(Integer::compareTo)
                    .orElse(0);

            Object[] rowData = {rank, username, score};
            tableModel.addRow(rowData);
        }
    }

    /**
     * Updates the table with scores for a specific game.
     * @param topUsers the list of top users
     * @param gameName the name of the game to display scores for
     */
    private void updateTableForGame(List<User> topUsers, String gameName) {
        // Clear existing rows
        tableModel.setRowCount(0);

        if (topUsers == null || topUsers.isEmpty()) {
            return;
        }

        // Add rows for each user
        for (int i = 0; i < topUsers.size(); i++) {
            User user = topUsers.get(i);
            int rank = i + 1;
            String username = user.getName();
            int score = user.getHighscores().getOrDefault(gameName, 0);

            Object[] rowData = {rank, username, score};
            tableModel.addRow(rowData);
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setLeaderBoardController(LeaderBoardController leaderBoardController) {
        this.leaderBoardController = leaderBoardController;
    }
}

