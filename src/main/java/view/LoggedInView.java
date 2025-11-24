package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.ChangePasswordController;
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
    private final ViewManagerModel viewManagerModel;  // Add this field
    private final JLabel passwordErrorField = new JLabel();
    private ChangePasswordController changePasswordController = null; // Keep the field for later use in case we use later
    private LogoutController logoutController;

    private final JLabel username;

    private final JButton logOut;
    private final JButton chessPuzzleButton;  // Add this field

    private final JTextField passwordInputField = new JTextField(15);
    private final JButton changePassword;

    public LoggedInView(LoggedInViewModel loggedInViewModel, ViewManagerModel viewManagerModel) {
        this.loggedInViewModel = loggedInViewModel;
        this.viewManagerModel = viewManagerModel;  // Initialize it
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

        // Add Chess Puzzle button
        chessPuzzleButton = new JButton("Play Chess Puzzles");
        buttons.add(chessPuzzleButton);

        logOut.addActionListener(
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

        // Add chess puzzle button listener
        chessPuzzleButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        if (evt.getSource().equals(chessPuzzleButton)) {
                            viewManagerModel.setState("chess puzzle");
                            viewManagerModel.firePropertyChange();
                        }
                    }
                }
        );

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

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

        changePassword.addActionListener(
                evt -> {
                    if (evt.getSource().equals(changePassword)) {
                        final LoggedInState currentState = loggedInViewModel.getState();

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
            System.out.println("Navigating to Screen Two");
            // Placeholder for new controller.execute() for Screen Two
        } else if (evt.getSource().equals(chessButton)) {
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
