package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginState;
import interface_adapter.login.LoginViewModel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * The View for when the user is logging into the program.
 */

public class LoginView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "log in";
    private final LoginViewModel loginViewModel;

    private final JTextField usernameInputField = new JTextField(15);
    private final JLabel usernameErrorField = new JLabel();

    private final JPasswordField passwordInputField = new JPasswordField(15);
    private final JLabel passwordErrorField = new JLabel();

    private final JButton logIn;
    private final JButton goToSignUp;
    private LoginController loginController = null;

    public LoginView(LoginViewModel loginViewModel, ViewManagerModel viewManagerModel) {
        this.loginViewModel = loginViewModel;
        this.loginViewModel.addPropertyChangeListener(this);

        final Color customGray = new Color(0xD9D9D9);
        // --- 1. Component Initialization and Configuration (Consolidated) ---

        final JLabel title = new JLabel("GameGrid"); // Adjusted text to match image
        // Title font size kept at 80f as requested
        title.setFont(FontLoader.jersey10.deriveFont(Font.PLAIN, 80f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setForeground(Color.BLACK);

        final JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(FontLoader.jersey10.deriveFont(Font.PLAIN, 40f));
        usernameLabel.setForeground(Color.BLACK);
        usernameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5)); // ADD MARGIN
        final JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(FontLoader.jersey10.deriveFont(Font.PLAIN, 40f));
        passwordLabel.setForeground(Color.BLACK);
        passwordLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5)); // ADD MARGIN

        usernameInputField.setFont(FontLoader.jersey10.deriveFont(Font.PLAIN, 30f));
        usernameInputField.setForeground(Color.BLACK);
        usernameInputField.setBackground(customGray);
        usernameInputField.setBorder(null);
        usernameInputField.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0)); // ADD MARGIN
        passwordInputField.setFont(FontLoader.jersey10.deriveFont(Font.PLAIN, 30f));
        passwordInputField.setForeground(Color.BLACK);
        passwordInputField.setBackground(customGray);
        passwordInputField.setBorder(null);
        passwordInputField.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0)); // ADD MARGIN
        // Use the custom LabelTextPanel
        final LabelTextPanel usernameInfo = new LabelTextPanel(usernameLabel, usernameInputField);
        final LabelTextPanel passwordInfo = new LabelTextPanel(passwordLabel, passwordInputField);

        // Ensure sub-panels are left-aligned
        usernameInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameErrorField.setForeground(Color.RED);
        passwordErrorField.setForeground(Color.RED);
        Dimension zeroHeight = new Dimension(Integer.MAX_VALUE, 0);
        usernameErrorField.setMaximumSize(zeroHeight);
        passwordErrorField.setMaximumSize(zeroHeight);

        final Border buttonPadding = BorderFactory.createEmptyBorder(
                3,
                20,
                3,
                20);
        // Configure button
        logIn = new JButton("Login");
        logIn.setFont(FontLoader.jersey10.deriveFont(Font.PLAIN, 40f));
        logIn.setForeground(Color.BLACK);
        logIn.setBackground(customGray);
        logIn.setBorder(buttonPadding);
        logIn.setBorderPainted(true); // Must be true for the border/padding to be respected
        logIn.setFocusPainted(false);
        goToSignUp = new JButton("Sign Up"); // Adjusted text to match image
        goToSignUp.setFont(FontLoader.jersey10.deriveFont(Font.PLAIN, 40f));
        goToSignUp.setForeground(Color.BLACK);
        goToSignUp.setBackground(customGray);
        goToSignUp.setBorder(buttonPadding);
        goToSignUp.setBorderPainted(true); // Must be true for the border/padding to be respected
        goToSignUp.setFocusPainted(false);
        // Buttons Panel Setup
        final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // FlowLayout(Left, hgap=0, vgap=0)
        buttons.add(logIn);
        buttons.add(Box.createHorizontalStrut(25));
        buttons.add(goToSignUp);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- 2. Transparency Setup (Consolidated) ---
        // Set all components to transparent BEFORE adding them to the form
        title.setOpaque(false);
        usernameLabel.setOpaque(false); // Make sure the label is transparent
        passwordLabel.setOpaque(false); // Make sure the label is transparent
        usernameInfo.setOpaque(false);
        passwordInfo.setOpaque(false);
        usernameErrorField.setOpaque(false);
        passwordErrorField.setOpaque(false);
        buttons.setOpaque(false);

        // Panel for vertical stacking and left alignment
        final JPanel formContentPanel = new JPanel();
        formContentPanel.setLayout(new BoxLayout(formContentPanel, BoxLayout.Y_AXIS));
        formContentPanel.setOpaque(false);

        // Add components to the vertical formContentPanel, using struts for spacing
        formContentPanel.add(title);
        formContentPanel.add(Box.createVerticalStrut(40)); // Spacing below title
        formContentPanel.add(usernameInfo);
        formContentPanel.add(usernameErrorField); // Error field appears below input
        formContentPanel.add(Box.createVerticalStrut(30));
        formContentPanel.add(passwordInfo);
        formContentPanel.add(passwordErrorField); // Error field appears below input
        formContentPanel.add(Box.createVerticalStrut(40));
        formContentPanel.add(buttons);
        formContentPanel.add(Box.createVerticalGlue());

        // --- 4. Main View and Background Setup ---

        ImagePanel background = new ImagePanel("/images/LoginScreenBackground.png");
        background.setLayout(new BorderLayout());

        // 1. Create the Left-Aligning Wrapper Panel
        // Use FlowLayout.LEFT to ensure formContentPanel is pushed to the left edge of
        // this wrapper
        final JPanel leftAlignmentWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // FlowLayout(align,
                                                                                               // hgap, vgap)
        leftAlignmentWrapper.setOpaque(false);

        // 2. Add the form content to the wrapper
        leftAlignmentWrapper.add(formContentPanel);

        // 3. Add the wrapper to the NORTH region of the background panel
        background.add(leftAlignmentWrapper, BorderLayout.NORTH);

        // 4. Apply the Top and Left margins to the wrapper's container (the background
        // panel)
        background.setBorder(BorderFactory.createEmptyBorder(100, 50, 0, 0)); // Top margin, Left margin

        this.setLayout(new BorderLayout()); // Set main panel layout
        this.add(background, BorderLayout.CENTER); // Add background to fill the main panel

        this.setLayout(new BorderLayout()); // Set main panel layout
        this.add(background, BorderLayout.CENTER); // Add background to fill the main panel

        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    logIn.doClick();
                }
            }
        };

        usernameInputField.addKeyListener(enterKeyListener);
        passwordInputField.addKeyListener(enterKeyListener);

        logIn.addActionListener(
                evt -> {
                    if (evt.getSource().equals(logIn)) {
                        final LoginState currentState = loginViewModel.getState();

                        // Check if controller is set before executing
                        if (loginController != null) {
                            loginController.execute(
                                    currentState.getUsername(),
                                    currentState.getPassword());
                        }
                    }
                });

        goToSignUp.addActionListener(
                evt -> {
                    if (evt.getSource().equals(goToSignUp)) {
                        viewManagerModel.setState("sign up");
                        viewManagerModel.firePropertyChange();
                    }
                });

        // Document listener helper function to reduce redundancy in listeners
        // for input fields
        DocumentListener inputFieldListener = new DocumentListener() {
            private void documentListenerHelper() {
                final LoginState currentState = loginViewModel.getState();
                currentState.setUsername(usernameInputField.getText());
                currentState.setPassword(new String(passwordInputField.getPassword()));
                loginViewModel.setState(currentState);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                documentListenerHelper();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                documentListenerHelper();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                documentListenerHelper();
            }
        };

        usernameInputField.getDocument().addDocumentListener(inputFieldListener);
        passwordInputField.getDocument().addDocumentListener(inputFieldListener);
    }

    /**
     * React to a button click that results in evt.
     *
     * @param evt the ActionEvent to react to
     */
    public void actionPerformed(ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final LoginState state = (LoginState) evt.getNewValue();
        setFields(state);
        usernameErrorField.setText(state.getLoginError());
    }

    private void setFields(LoginState state) {
        usernameInputField.setText(state.getUsername());
    }

    public String getViewName() {
        return viewName;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }
}
