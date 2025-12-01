package wordle;

import interface_adapter.ViewManagerModel;
import interface_adapter.leaderboard.LeaderBoardController;
import interface_adapter.wordle.WordleController;
import interface_adapter.wordle.WordleViewModel;

import view.FontLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

/**
 * Passive Swing view. UI events are forwarded to controller.
 * The presenter must call setViewModel(vm) via the provided viewUpdater to update the UI.
 */
public class WordleView extends JPanel {
    private final WordleController controller;
    private LeaderBoardController leaderBoardController;
    private final BoardPanel boardPanel;
    private final JTextField typingField;
    private final JLabel statusLabel;
    private final JLabel scoreLabel;
    private int score = 0; // number of games won
//    private final JLabel scoreLabel;
    private final JButton backButton;
    private ViewManagerModel viewManagerModel = null;

    private String userId = "default-user"; // replace with actual logged-in userId integration

    public WordleView(WordleController controller, ViewManagerModel viewManagerModel,
                      Consumer<WordleViewModel> vmConsumer) {
        this.controller = controller;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // stack board and bottom vertically

        setBackground(new Color(45, 45, 45));
        boardPanel = new BoardPanel();

        JPanel boardContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        boardContainer.add(boardPanel);
        boardContainer.setAlignmentX(Component.CENTER_ALIGNMENT); // center horizontally
        boardContainer.setBackground(new Color(45, 45, 45));

        // Create floating logo
        JLabel logo = new JLabel("WORDLE");
        logo.setFont(FontLoader.jersey10.deriveFont(72f)); // very large font
        logo.setForeground(Color.WHITE);
        logo.setSize(200, 100); // width and height of label
        logo.setLocation(500, 50); // adjust x/y to fit the empty space on the right
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setAlignmentY(Component.CENTER_ALIGNMENT);

// Add logo to the WordleView panel (not inside boardContainer)
        add(Box.createVerticalStrut(20)); // adds 50px of space above the logo
        add(logo);


        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(WordleStyles.KEY_FONT.deriveFont(18f));
        add(scoreLabel);


        add(Box.createVerticalStrut(10));

        logo.setVisible(true);


        //add(Box.createVerticalStrut(20)); // top spacing
        add(boardContainer);

        boardContainer.add(boardPanel);


        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBackground(new Color(45, 45, 45)); // explicit dark grey
        typingField = new JTextField();
        typingField.setAlignmentX(Component.CENTER_ALIGNMENT);
        typingField.setFont(WordleStyles.KEY_FONT.deriveFont(20f));
        typingField.setHorizontalAlignment(JTextField.CENTER);
        typingField.setColumns(7);
        typingField.setMaximumSize(new Dimension(200, 40));


        // Pressing Enter on keyboard submits the guess
        typingField.addActionListener(e -> doSubmit());
        // Prevent Tab from moving focus so we can capture it
        typingField.setFocusTraversalKeysEnabled(false);

        typingField.addKeyListener(new KeyAdapter() {
            private boolean newGameMode = false;

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    e.consume(); // prevent default focus traversal
                    newGameMode = true;
                    statusLabel.setText("Press ENTER to start a new game");
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER && newGameMode) {
                    controller.startNewGame(userId);
                    typingField.setText("");
                    typingField.requestFocusInWindow();
                    statusLabel.setText("New game started!");
                    newGameMode = false;
                } else if (newGameMode) {
                    // Any other key cancels new game mode
                    newGameMode = false;
                    statusLabel.setText(" ");
                }
            }
        });


        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controls.setBackground(new Color(45, 45, 45));
        controls.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton newGame = new JButton("New Game");
        newGame.setFont(FontLoader.jersey10.deriveFont(20f)); // set jersey10 font, size 16
        newGame.setBackground(new Color(70, 130, 180)); // example: green
        newGame.setForeground(Color.WHITE); // text color
        newGame.setOpaque(true);
        newGame.setBorderPainted(false);
        newGame.addActionListener(e -> {
            controller.startNewGame(userId);
            typingField.setText("");
            typingField.requestFocusInWindow();
            //keyboardPanel.resetKeys();
        });


        controls.add(newGame);

        JButton submit = new JButton("Submit");
        submit.setFont(FontLoader.jersey10.deriveFont(20f));
        submit.setBackground(new Color(34, 139, 34)); // steel blue example
        submit.setForeground(Color.WHITE);
        submit.setOpaque(true);
        submit.setBorderPainted(false);
        submit.addActionListener(e -> doSubmit());

        controls.add(submit);

        JButton clear = new JButton("Clear");
        clear.setFont(FontLoader.jersey10.deriveFont(20f));
        clear.setBackground(new Color(220, 20, 60)); // crimson example
        clear.setForeground(Color.WHITE);
        clear.setOpaque(true);
        clear.setBorderPainted(false);
        clear.addActionListener(e -> typingField.setText(""));

        controls.add(clear);

        // add back button

        this.viewManagerModel = viewManagerModel;

        backButton = new JButton("Back");
        backButton.setFont(FontLoader.jersey10.deriveFont(18f));
        backButton.setBackground(new Color(128, 128, 128));
        backButton.setForeground(Color.WHITE);
        backButton.setOpaque(true);
        backButton.setBorderPainted(false);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> {
            // Switch to LoggedInView
            viewManagerModel.setState("logged in");
            viewManagerModel.firePropertyChange();
        });

        controls.add(backButton);

        // add leaderboard button
        JButton leaderboardButton = new JButton("Leaderboard");
        leaderboardButton.setFont(FontLoader.jersey10.deriveFont(18f));
        leaderboardButton.setBackground(new Color(255, 215, 0)); // Gold color
        leaderboardButton.setForeground(Color.BLACK);
        leaderboardButton.setOpaque(true);
        leaderboardButton.setBorderPainted(false);
        leaderboardButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        leaderboardButton.addActionListener(e -> {
            // Switch to LeaderBoardView
            viewManagerModel.setState("leaderboard");
            viewManagerModel.firePropertyChange();
        });

        controls.add(leaderboardButton);


        bottom.add(typingField, BorderLayout.NORTH);

        KeyboardPanel keyboard = new KeyboardPanel(new KeyListener());
        keyboard.setBackground(new Color(45, 45, 45));
        keyboard.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottom.add(keyboard, BorderLayout.CENTER);
        bottom.add(controls, BorderLayout.SOUTH);

        statusLabel = new JLabel(" ");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(WordleStyles.KEY_FONT.deriveFont(15f));
        add(statusLabel);

        add(bottom);
        add(Box.createVerticalStrut(20)); // optional bottom spacing


        // expose vm consumer so presenter can update UI
        vmConsumer.accept(null); // no-op initial
        // immediately start a new game when the user arrives here
        controller.startNewGame(userId);
        SwingUtilities.invokeLater(() -> typingField.requestFocusInWindow());

    }

    public void setViewModel(WordleViewModel vm) {
        if (vm == null) return;
        boardPanel.setViewModel(vm);
        statusLabel.setText(vm.message != null ? vm.message : " ");
        // Update status message
        if (vm.finished) {
            int updatedScore = controller.getScore(userId);
            scoreLabel.setText("Score: " + updatedScore);
            if (vm.won) {
                score++;
                statusLabel.setText("You win! TAB + ENTER to play again");
                scoreLabel.setText("Score: " + score);
                // Submit score to leaderboard
                submitScoreToLeaderboard(updatedScore);
            } else {
                statusLabel.setText("You lose! The answer was "
                        + vm.answerIfFinished.toUpperCase()
                        + ". TAB + ENTER for a new game");
                score--;
                if (score < 0) score = 0;
                scoreLabel.setText("Score: " + score);
                statusLabel.setText("You lose! The answer was " + vm.answerIfFinished.toUpperCase() + ". To play again, TAB + ENTER");
                // Submit score to leaderboard even on loss (to track total games)
                submitScoreToLeaderboard(updatedScore);
            }
        } else {
            // Normal message
            statusLabel.setText(vm.message != null ? vm.message : " ");
        }

        repaint();
    }

    private void submitScoreToLeaderboard(int finalScore) {
        if (leaderBoardController != null && userId != null) {
            // Submit to leaderboard - it will check if it's a new highscore
            leaderBoardController.execute(userId, finalScore, "Wordle");
        }
    }

    public void setLeaderBoardController(LeaderBoardController leaderBoardController) {
        this.leaderBoardController = leaderBoardController;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private void doSubmit() {
        String txt = typingField.getText().trim().toLowerCase();
        controller.submitGuess(userId, txt);
        typingField.setText(""); // clears text field once user submits
    }

    private class KeyListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();

            // Backspace button
            if ("BACK".equals(cmd) || "<-".equals(cmd)) {
                String t = typingField.getText();
                if (!t.isEmpty()) typingField.setText(t.substring(0, t.length() - 1));
                return;
            }

            // Letters and normal keys
            if ("ENTER".equals(cmd)) {
                doSubmit();
                return;
            }

            // Append letters
            typingField.setText(typingField.getText() + cmd.toLowerCase());
        }
    }
}
