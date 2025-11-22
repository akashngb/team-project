package view;

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
    private final JButton menuButton1;
    private final JButton menuButton2;
    private final JButton menuButton3;
    private final JButton logOut; // Keep the Log Out button
    private final ImagePanel background;

    public LoggedInView(LoggedInViewModel loggedInViewModel) {
        this.loggedInViewModel = loggedInViewModel;
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

        // Game 1: Block Blast
        URL blockBlastImageUrl = getClass().getResource("/images/blockblast.png");
        ImageIcon blockBlastIcon = new ImageIcon(blockBlastImageUrl);
        blockBlastIcon = new ImageIcon(blockBlastIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH)); // Scale image
        JLabel blockBlastImage = new JLabel(blockBlastIcon);
        JLabel blockBlastTitle = new JLabel("Block Blast", SwingConstants.CENTER);
        blockBlastTitle.setFont(gameTitleFont);

        JPanel blockBlastPanel = new JPanel();
        blockBlastPanel.setLayout(new BorderLayout()); // Use BorderLayout for image and text
        blockBlastPanel.add(blockBlastImage, BorderLayout.CENTER);
        blockBlastPanel.add(blockBlastTitle, BorderLayout.SOUTH);
        blockBlastPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add some padding
        blockBlastPanel.setOpaque(false);
        // Make the panel clickable
        menuButton1 = new JButton(); // Use a JButton for click functionality
        menuButton1.setLayout(new BorderLayout());
        menuButton1.add(blockBlastPanel, BorderLayout.CENTER); // Add the content panel to the button
        menuButton1.setOpaque(false); // Make button transparent to show panel background
        menuButton1.setContentAreaFilled(false);
        menuButton1.setBorderPainted(false);
        menuButton1.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Indicate it's clickable


        // Game 2: Wordle
        URL wordleImageUrl = getClass().getResource("/images/wordle.png");
        ImageIcon wordleIcon = new ImageIcon(wordleImageUrl);
        wordleIcon = new ImageIcon(wordleIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH));
        JLabel wordleImage = new JLabel(wordleIcon);
        JLabel wordleTitle = new JLabel("Wordle", SwingConstants.CENTER);
        wordleTitle.setFont(gameTitleFont);

        JPanel wordlePanel = new JPanel();
        wordlePanel.setLayout(new BorderLayout());
        wordlePanel.add(wordleImage, BorderLayout.CENTER);
        wordlePanel.add(wordleTitle, BorderLayout.SOUTH);
        wordlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        wordlePanel.setOpaque(false);

        menuButton2 = new JButton();
        menuButton2.setLayout(new BorderLayout());
        menuButton2.add(wordlePanel, BorderLayout.CENTER);
        menuButton2.setOpaque(false);
        menuButton2.setContentAreaFilled(false);
        menuButton2.setBorderPainted(false);
        menuButton2.setCursor(new Cursor(Cursor.HAND_CURSOR));


        // Game 3: Chess Puzzles
        URL chessImageUrl = getClass().getResource("/images/chess.png");
        ImageIcon chessIcon = new ImageIcon(chessImageUrl);
        chessIcon = new ImageIcon(chessIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH));
        JLabel chessImage = new JLabel(chessIcon);
        JLabel chessTitle = new JLabel("Chess Puzzles", SwingConstants.CENTER);
        chessTitle.setFont(gameTitleFont);

        JPanel chessPanel = new JPanel();
        chessPanel.setLayout(new BorderLayout());
        chessPanel.add(chessImage, BorderLayout.CENTER);
        chessPanel.add(chessTitle, BorderLayout.SOUTH);
        chessPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        chessPanel.setOpaque(false);

        menuButton3 = new JButton();
        menuButton3.setLayout(new BorderLayout());
        menuButton3.add(chessPanel, BorderLayout.CENTER);
        menuButton3.setOpaque(false);
        menuButton3.setContentAreaFilled(false);
        menuButton3.setBorderPainted(false);
        menuButton3.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Log Out Button (retained and styled)
        logOut = new JButton("Log Out");
        logOut.setFont(buttonFont);
        logOut.setOpaque(false);

        background = new ImagePanel("/images/LoginScreenBackground.png");
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));

        this.add(background);

        // Horizontal Layout for Game Selection Panels
        final JPanel gameSelectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0)); // Center with more horizontal gap
        gameSelectionPanel.add(menuButton1); // Now these are the clickable game panels
        gameSelectionPanel.add(menuButton2);
        gameSelectionPanel.add(menuButton3);
        gameSelectionPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameSelectionPanel.setOpaque(false);
        // Main Content Panel (Vertical Stacking)
        final JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        contentPanel.add(gameSelectionPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 40))); // More space before Log Out
        contentPanel.add(logOut);
        logOut.setAlignmentX(Component.CENTER_ALIGNMENT); // Ensure log out button is centered

        // Add Action Listeners
        menuButton1.addActionListener(this);
        menuButton2.addActionListener(this);
        menuButton3.addActionListener(this);

        // Action Listener for Log Out
        logOut.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        if (evt.getSource().equals(logOut)) {
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
        final String defaultBackgroundPath = "/images/LoginScreenBackground.png";
        MouseAdapter hoverAdapter = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                JButton button = (JButton) e.getSource();
                String newBackgroundPath = defaultBackgroundPath;

                if (button.equals(menuButton1)) {
                    newBackgroundPath = "/images/blockblast_background.png"; // CHANGE TO BLOCKBLAST BACKGROUND PATH
                } else if (button.equals(menuButton2)) {
                    newBackgroundPath = "/images/wordle_background.png"; // CHANGE TO WORDLE BACKGROUND PATH
                } else if (button.equals(menuButton3)) {
                    newBackgroundPath = "/images/chess_background.png"; // CHANGE TO CHESS BACKGROUND PATH
                }

                // Set the new background image
                background.setBackgroundImage(newBackgroundPath); // ASSUMES ImagePanel has this setter
                background.repaint(); // Force a redraw of the background

                // Apply the floating border (keep old logic)
                Border hoverBorder = BorderFactory.createRaisedBevelBorder();
                button.setBorderPainted(true);
                button.setBorder(hoverBorder);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                JButton button = (JButton) e.getSource();

                // Reset the background image to the default
                background.setBackgroundImage(defaultBackgroundPath);
                background.repaint();

                // Reset the floating border (keep old logic)
                button.setBorder(defaultBorder);
                button.setBorderPainted(false);
            }
        };

        // Apply the hover effect adapter to all three game selection buttons
        menuButton1.addMouseListener(hoverAdapter);
        menuButton2.addMouseListener(hoverAdapter);
        menuButton3.addMouseListener(hoverAdapter);

        // Title ("Select Game")
        background.add(title);

        // Spacing between Title and Game Icons (increased spacing for visual appeal)
        background.add(Box.createRigidArea(new Dimension(0, 50)));

        // Main Content (Game Icons + Log Out Button)
        background.add(contentPanel);

    }

    /**
     * React to a button click that results in evt.
     * @param evt the ActionEvent to react to
     */
    public void actionPerformed(ActionEvent evt) {
        // Implement navigation logic here later. For now, print a message.
        if (evt.getSource().equals(menuButton1)) {
            System.out.println("Navigating to Screen One");
            // Placeholder for new controller.execute() for Screen One
        } else if (evt.getSource().equals(menuButton2)) {
            System.out.println("Navigating to Screen Two");
            // Placeholder for new controller.execute() for Screen Two
        } else if (evt.getSource().equals(menuButton3)) {
            System.out.println("Navigating to Screen Three");
            // Placeholder for new controller.execute() for Screen Three
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