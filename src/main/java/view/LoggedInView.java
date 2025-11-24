package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.ChangePasswordController;
import interface_adapter.logged_in.ChangePasswordController; // Keep import for setter
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.logout.LogoutController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.Border;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;

/**
 * The Menu View displayed after the user logs in.
 */
public class LoggedInView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "logged in";
    private final LoggedInViewModel loggedInViewModel;
    private final JLabel passwordErrorField = new JLabel();
    private ChangePasswordController changePasswordController = null; // Keep the field for later use in case we use later
    private LogoutController logoutController;

    // New buttons for the menu options
    private final JButton blockBlastButton;
    private final JButton wordleButton;
    private final JButton chessButton;
    private final JButton logOutButton; // Keep the Log Out button
    private final ImagePanel backgroundScreen;
    private JButton currentlySelectedButton;

    private JButton createGameButton(String gameName, String imagePath, Font gameTitleFont) {
        // Load the image resource
        URL imageUrl = getClass().getResource(imagePath);
        ImageIcon icon = new ImageIcon(imageUrl);
        icon = new ImageIcon(icon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH));

        // Create the title label
        JLabel image = new JLabel(icon);
        JLabel title = new JLabel(gameName, SwingConstants.CENTER);
        title.setFont(gameTitleFont);

        // Create the content panel (Image + Title)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(image, BorderLayout.CENTER);
        contentPanel.add(title, BorderLayout.SOUTH);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.setOpaque(false);

        // Create the transparent button wrapper
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.add(contentPanel, BorderLayout.CENTER);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private final ViewManagerModel viewManagerModel;

    public LoggedInView(LoggedInViewModel loggedInViewModel, ViewManagerModel viewManagerModel) {
        this.loggedInViewModel = loggedInViewModel;
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel.addPropertyChangeListener(this);

        // Font Variables for Centralized Styling
        final Font titleFont = FontLoader.jersey10.deriveFont(Font.BOLD, 48f);
        final Font gameTitleFont = FontLoader.jersey10.deriveFont(Font.PLAIN, 30f);
        final Font buttonFont = FontLoader.jersey10.deriveFont(Font.BOLD, 18f);

        // Apply Font to Title (Using FontLoader as in LoginView)
        final JLabel title = new JLabel("Select Game");
        title.setFont(titleFont);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setOpaque(false);
        // Game Panels and Buttons

        blockBlastButton = createGameButton("Block Blast", "/images/blockblast.png", gameTitleFont);
        wordleButton = createGameButton("Wordle", "/images/wordle.png", gameTitleFont);
        chessButton = createGameButton("Chess Puzzles", "/images/chess.png", gameTitleFont);

        currentlySelectedButton = blockBlastButton;

        // Log Out Button (retained and styled)
        logOutButton = new JButton("Log Out");
        logOutButton.setFont(buttonFont);
        logOutButton.setOpaque(false);

        backgroundScreen = new ImagePanel("/images/blockblast_background.png");
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        backgroundScreen.setLayout(new BoxLayout(backgroundScreen, BoxLayout.Y_AXIS));

        this.add(backgroundScreen);

        // Horizontal Layout for Game Selection Panels
        final JPanel gameSelectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0)); // Center with more horizontal gap
        gameSelectionPanel.add(blockBlastButton); // Now these are the clickable game panels
        gameSelectionPanel.add(wordleButton);
        gameSelectionPanel.add(chessButton);
        gameSelectionPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameSelectionPanel.setOpaque(false);
        // Main Content Panel (Vertical Stacking)
        final JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        contentPanel.add(gameSelectionPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 40))); // More space before Log Out
        contentPanel.add(logOutButton);
        logOutButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Ensure log out button is centered

        // Add Action Listeners
        blockBlastButton.addActionListener(this);
        wordleButton.addActionListener(this);
        chessButton.addActionListener(this);

        // Action Listener for Log Out
        logOutButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        if (evt.getSource().equals(logOutButton)) {
                            if (logoutController != null) {
                                logoutController.execute();
                            }
                        }
                    }
                }
        );
        // Apply Hover/Float Effect

        // Define the default border only (no need for a static hoverBorder)
        final Border defaultBorder = BorderFactory.createEmptyBorder();
        final String defaultBackgroundPath = "/images/blockblast_background.png";
        MouseAdapter hoverAdapter = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                JButton button = (JButton) e.getSource();
                String newBackgroundPath = defaultBackgroundPath;

                // 1. Remove the border from the currently selected button (the old one)
                if (currentlySelectedButton != null) {
                    currentlySelectedButton.setBorderPainted(false);
                    currentlySelectedButton.setBorder(defaultBorder);
                }

                // 2. Determine the new path (the paths are the same as the default background, but we keep the logic clean)
                if (button.equals(blockBlastButton)) {
                    newBackgroundPath = "/images/blockblast_background.png";
                } else if (button.equals(wordleButton)) {
                    newBackgroundPath = "/images/wordle_background.png";
                } else if (button.equals(chessButton)) {
                    newBackgroundPath = "/images/chess_background.jpg";
                }

                // 3. Set the new background image
                backgroundScreen.setBackgroundImage(newBackgroundPath);
                backgroundScreen.repaint();

                // 4. Apply the floating border (the hover effect) to the current button
                Border hoverBorder = BorderFactory.createRaisedBevelBorder();
                button.setBorderPainted(true);
                button.setBorder(hoverBorder);

                // 5. IMPORTANT: Update the currently selected button
                currentlySelectedButton = button;
            }
        };

        // Apply the hover effect adapter to all three game selection buttons
        blockBlastButton.addMouseListener(hoverAdapter);
        wordleButton.addMouseListener(hoverAdapter);
        chessButton.addMouseListener(hoverAdapter);

        Border initialSelectedBorder = BorderFactory.createRaisedBevelBorder();
        currentlySelectedButton.setBorderPainted(true);
        currentlySelectedButton.setBorder(initialSelectedBorder);

        backgroundScreen.add(Box.createRigidArea(new Dimension(0, 30)));

        // Title ("Select Game")
        backgroundScreen.add(title);

        // Spacing between Title and Game Icons (increased spacing for visual appeal)
        backgroundScreen.add(Box.createRigidArea(new Dimension(0, 50)));

        // Main Content (Game Icons + Log Out Button)
        backgroundScreen.add(contentPanel);

    }

    /**
     * React to a button click that results in evt.
     * @param evt the ActionEvent to react to
     */
    public void actionPerformed(ActionEvent evt) {
        // Implement navigation logic here later. For now, print a message.
        if (evt.getSource().equals(blockBlastButton)) {
            System.out.println("Navigating to Screen One");
            // Placeholder for new controller.execute() for Screen One
        } else if (evt.getSource().equals(wordleButton)) {
            viewManagerModel.setState("WORDLE");
            viewManagerModel.firePropertyChange();
        } else if (evt.getSource().equals(chessButton)) {
            viewManagerModel.setState("CHESS-PUZZLE");
            viewManagerModel.firePropertyChange();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            final LoggedInState state = (LoggedInState) evt.getNewValue();
        }
        else if (evt.getPropertyName().equals("password")) {
            // Keep password related UI update logic, even if the password field is gone
            final LoggedInState state = (LoggedInState) evt.getNewValue();
            if (state.getPasswordError() == null) {
                JOptionPane.showMessageDialog(this, "password updated for " + state.getUsername());
                // Removed passwordInputField.setText("")
            }
            else {
                JOptionPane.showMessageDialog(this, state.getPasswordError());
            }
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setChangePasswordController(ChangePasswordController changePasswordController) {
        this.changePasswordController = changePasswordController;
    }

    public void setLogoutController(LogoutController logoutController) {
        this.logoutController = logoutController;
    }
}