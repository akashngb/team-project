package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.ChangePasswordController;
import interface_adapter.logged_in.ChangePasswordController; // Keep import for setter
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.logout.LogoutController;
import app.blockblast.BlockBlastAppBuilder;

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
    private LogoutController logoutController;

    // New buttons for the menu options
    private final JButton blockBlastButton;
    private final JButton wordleButton;
    private final JButton chessButton;
    private final JButton logOutButton; // Keep the Log Out button
    private final FadingImagePanel backgroundScreen;
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
        title.setForeground(Color.BLACK); // Ensure text is visible against the light background

        // Create the semi-transparent background color (e.g., White with 70% opacity)
        final Color faintBackground = new Color(255, 255, 255, 180);

        // Create the content panel (Image + Title)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        contentPanel.setBackground(faintBackground); // Apply faint background color
        contentPanel.setOpaque(true); // Make the panel fully opaque so the background color is visible

        contentPanel.add(image, BorderLayout.CENTER);
        contentPanel.add(title, BorderLayout.SOUTH);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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
        final Font titleFont = FontLoader.jersey10.deriveFont(Font.PLAIN, 80f);
        final Font gameTitleFont = FontLoader.jersey10.deriveFont(Font.PLAIN, 50f);
        final Font buttonFont = FontLoader.jersey10.deriveFont(Font.PLAIN, 40f);
        final Color customGray = new Color(0xD9D9D9);
        final Border buttonPadding = BorderFactory.createEmptyBorder(3, 20, 3, 20); // 3px T/B, 20px L/R
        // Apply Font to Title (Using FontLoader as in LoginView)
        final JLabel title = new JLabel("Select Game");
        title.setFont(titleFont);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setOpaque(false);
        title.setForeground(Color.WHITE);
        // Game Panels and Buttons

        blockBlastButton = createGameButton("Block Blast", "/images/blockblast.png", gameTitleFont);
        wordleButton = createGameButton("Wordle", "/images/wordle.png", gameTitleFont);
        chessButton = createGameButton("Chess Puzzles", "/images/chess.png", gameTitleFont);

        currentlySelectedButton = blockBlastButton;

        // Log Out Button (retained and styled)
        logOutButton = new JButton("Log Out");
        logOutButton.setFont(buttonFont);
        logOutButton.setForeground(Color.BLACK); // Set text color to black
        logOutButton.setBackground(customGray); // Set background color to #D9D9D9
        logOutButton.setBorder(buttonPadding); // Apply padding border
        logOutButton.setBorderPainted(true); // Ensure padding is rendered
        logOutButton.setFocusPainted(false); // Remove focus border

        backgroundScreen = new FadingImagePanel("/images/blockblast_background.jpg");
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        backgroundScreen.setLayout(new BoxLayout(backgroundScreen, BoxLayout.Y_AXIS));

        this.add(backgroundScreen);

        // Horizontal Layout for Game Selection Panels
        final JPanel gameSelectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0)); // Center with more
                                                                                                // horizontal gap
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
        contentPanel.add(Box.createVerticalStrut(50));
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
                });
        // Apply Hover/Float Effect

        // Define the default border only (no need for a static hoverBorder)
        final Border defaultBorder = BorderFactory.createEmptyBorder();
        final String defaultBackgroundPath = "/images/blockblast_background.jpg";
        MouseAdapter hoverAdapter = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                JButton button = (JButton) e.getSource();
                String newBackgroundPath = defaultBackgroundPath;

                if (currentlySelectedButton != null) {
                    currentlySelectedButton.setBorderPainted(false);
                    currentlySelectedButton.setBorder(defaultBorder);
                }

                if (button.equals(blockBlastButton)) {
                    newBackgroundPath = "/images/blockblast_background.jpg";
                } else if (button.equals(wordleButton)) {
                    newBackgroundPath = "/images/wordle_background.jpg";
                } else if (button.equals(chessButton)) {
                    newBackgroundPath = "/images/chess_background.jpg";
                }

                backgroundScreen.setBackgroundImage(newBackgroundPath);

                Border hoverBorder = BorderFactory.createRaisedBevelBorder();
                button.setBorderPainted(true);
                button.setBorder(hoverBorder);

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
     * 
     * @param evt the ActionEvent to react to
     */
    public void actionPerformed(ActionEvent evt) {
        // Implement navigation logic here later. For now, print a message.
        if (evt.getSource().equals(blockBlastButton)) {
            JFrame frame = BlockBlastAppBuilder.buildFrame();
            frame.setVisible(true);
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
        } else if (evt.getPropertyName().equals("password")) {
            // Keep password related UI update logic, even if the password field is gone
            final LoggedInState state = (LoggedInState) evt.getNewValue();
            if (state.getPasswordError() == null) {
                JOptionPane.showMessageDialog(this, "password updated for " + state.getUsername());
                // Removed passwordInputField.setText("")
            } else {
                JOptionPane.showMessageDialog(this, state.getPasswordError());
            }
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setLogoutController(LogoutController logoutController) {
        this.logoutController = logoutController;
    }
}
