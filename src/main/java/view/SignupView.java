package view;

import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupState;
import interface_adapter.signup.SignupViewModel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JRootPane;
import java.awt.event.HierarchyEvent;
import javax.swing.SwingUtilities;
/**
 * The View for the Signup Use Case, now visually matching LoginView.
 */
public class SignupView extends JPanel implements ActionListener, PropertyChangeListener {
    private final String viewName = "sign up";

    private final SignupViewModel signupViewModel;
    
    // Inputs from original file
    private final JTextField usernameInputField = new JTextField(15);
    private final JPasswordField passwordInputField = new JPasswordField(15);
    private final JPasswordField repeatPasswordInputField = new JPasswordField(15);
    
    // Error fields to hold visual space (matching LoginView structure)
    private final JLabel usernameErrorField = new JLabel();
    private final JLabel passwordErrorField = new JLabel();
    private final JLabel repeatPasswordErrorField = new JLabel();

    private SignupController signupController = null;

    private final JButton signUp;
    private final JButton toLogin;

    /**
     * Corrected Constructor: Takes only SignupViewModel, preserving original signature.
     */
    public SignupView(SignupViewModel signupViewModel) {
        this.signupViewModel = signupViewModel;
        signupViewModel.addPropertyChangeListener(this);

        final Color customGray = new Color(0xD9D9D9);
        final Dimension zeroHeight = new Dimension(Integer.MAX_VALUE, 0);

        // --- 1. Component Initialization and Configuration (Visual Mimicry) ---

        // Title
        final JLabel title = new JLabel("GameGrid"); 
        title.setFont(FontLoader.jersey10.deriveFont(Font.PLAIN, 80f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setForeground(Color.BLACK);

        // Labels
        final int labelFontSize = 40;
        final JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(FontLoader.jersey10.deriveFont(Font.PLAIN, labelFontSize));
        usernameLabel.setForeground(Color.BLACK);
        usernameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        
        final JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(FontLoader.jersey10.deriveFont(Font.PLAIN, labelFontSize));
        passwordLabel.setForeground(Color.BLACK);
        passwordLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        
        final JLabel repeatPasswordLabel = new JLabel("Repeat:");
        repeatPasswordLabel.setFont(FontLoader.jersey10.deriveFont(Font.PLAIN, labelFontSize));
        repeatPasswordLabel.setForeground(Color.BLACK);
        repeatPasswordLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));


        // Input Fields (Applying LoginView's styles)
        final int inputFontSize = 30;
        final int inputMargin = 5;
        final Border inputBorder = BorderFactory.createEmptyBorder(0, inputMargin, 0, 0);
        
        usernameInputField.setFont(FontLoader.jersey10.deriveFont(Font.PLAIN, inputFontSize));
        usernameInputField.setForeground(Color.BLACK);
        usernameInputField.setBackground(customGray);
        usernameInputField.setBorder(inputBorder);
        
        passwordInputField.setFont(FontLoader.jersey10.deriveFont(Font.PLAIN, inputFontSize));
        passwordInputField.setForeground(Color.BLACK);
        passwordInputField.setBackground(customGray);
        passwordInputField.setBorder(inputBorder);
        
        repeatPasswordInputField.setFont(FontLoader.jersey10.deriveFont(Font.PLAIN, inputFontSize));
        repeatPasswordInputField.setForeground(Color.BLACK);
        repeatPasswordInputField.setBackground(customGray);
        repeatPasswordInputField.setBorder(inputBorder);

        // LabelTextPanel Instances
        final LabelTextPanel usernameInfo = new LabelTextPanel(usernameLabel, usernameInputField);
        final LabelTextPanel passwordInfo = new LabelTextPanel(passwordLabel, passwordInputField);
        final LabelTextPanel repeatPasswordInfo = new LabelTextPanel(repeatPasswordLabel, repeatPasswordInputField);

        // Error Fields (Matching LoginView's collapse logic setup)
        usernameErrorField.setForeground(Color.RED);
        passwordErrorField.setForeground(Color.RED);
        repeatPasswordErrorField.setForeground(Color.RED);
        usernameErrorField.setMaximumSize(zeroHeight);
        passwordErrorField.setMaximumSize(zeroHeight);
        repeatPasswordErrorField.setMaximumSize(zeroHeight);


        // Buttons (Applying LoginView's styles and padding)
        final int buttonFontSize = 40;
        final Border buttonPadding = BorderFactory.createEmptyBorder(3, 20, 3, 20);
        final int spacingBetweenButtons = 25;

        // Note: Using the original ViewModel labels for text, but keeping the visual style.
        toLogin = new JButton(SignupViewModel.TO_LOGIN_BUTTON_LABEL); 
        toLogin.setFont(FontLoader.jersey10.deriveFont(Font.PLAIN, buttonFontSize));
        toLogin.setForeground(Color.BLACK);
        toLogin.setBackground(customGray);
        toLogin.setBorder(buttonPadding);
        toLogin.setBorderPainted(true);
        toLogin.setFocusPainted(false);
        
        signUp = new JButton(SignupViewModel.SIGNUP_BUTTON_LABEL); 
        signUp.setFont(FontLoader.jersey10.deriveFont(Font.PLAIN, buttonFontSize));
        signUp.setForeground(Color.BLACK);
        signUp.setBackground(customGray);
        signUp.setBorder(buttonPadding);
        signUp.setBorderPainted(true);
        signUp.setFocusPainted(false);
        
        // Buttons Panel Setup (Matching LoginView's left-aligned spacing)
        final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); 
        // Order: toLogin ("Go to Login") then signUp ("Sign up")
        buttons.add(toLogin);
        buttons.add(Box.createHorizontalStrut(spacingBetweenButtons));
        buttons.add(signUp);
        
        // Ensure panels are left-aligned
        usernameInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        repeatPasswordInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);


        // --- 2. Transparency Setup ---
        title.setOpaque(false);
        usernameLabel.setOpaque(false);
        passwordLabel.setOpaque(false);
        repeatPasswordLabel.setOpaque(false);
        usernameInfo.setOpaque(false);
        passwordInfo.setOpaque(false);
        repeatPasswordInfo.setOpaque(false);
        usernameErrorField.setOpaque(false);
        passwordErrorField.setOpaque(false);
        repeatPasswordErrorField.setOpaque(false);
        buttons.setOpaque(false);

        // --- 3. Layout Structure (Consolidated) ---

        // Panel for vertical stacking and left alignment
        final JPanel formContentPanel = new JPanel();
        formContentPanel.setLayout(new BoxLayout(formContentPanel, BoxLayout.Y_AXIS));
        formContentPanel.setOpaque(false);

        // Add components to the vertical formContentPanel, using struts for spacing
        formContentPanel.add(title);
        formContentPanel.add(Box.createVerticalStrut(40)); 
        
        // Username
        formContentPanel.add(usernameInfo);
        formContentPanel.add(usernameErrorField); 
        formContentPanel.add(Box.createVerticalStrut(30));
        
        // Password
        formContentPanel.add(passwordInfo);
        formContentPanel.add(passwordErrorField);
        formContentPanel.add(Box.createVerticalStrut(30)); 
        
        // Repeat Password
        formContentPanel.add(repeatPasswordInfo);
        formContentPanel.add(repeatPasswordErrorField);
        formContentPanel.add(Box.createVerticalStrut(40)); 
        
        // Buttons
        formContentPanel.add(buttons);
        formContentPanel.add(Box.createVerticalGlue());

        // --- 4. Main View and Background Setup (LoginView's NORTHWEST Layout) ---

        ImagePanel background = new ImagePanel("/images/LoginScreenBackground.png");
        background.setLayout(new BorderLayout());

        // 1. Create the Left-Aligning Wrapper Panel
        final JPanel leftAlignmentWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftAlignmentWrapper.setOpaque(false);
        leftAlignmentWrapper.add(formContentPanel);

        // 2. Add the wrapper to the NORTH region of the background panel
        background.add(leftAlignmentWrapper, BorderLayout.NORTH);

        // 3. Apply the Top and Left margins
        background.setBorder(BorderFactory.createEmptyBorder(100, 50, 0, 0)); 

        this.setLayout(new BorderLayout()); 
        this.add(background, BorderLayout.CENTER); 

        // --- 5. Listeners and Controllers (Consolidated) ---
        
        // Document listener helper function to reduce redundancy in listeners
        DocumentListener inputFieldListener = new DocumentListener() {
            private void documentListenerHelper() {
                final SignupState currentState = signupViewModel.getState();
                currentState.setUsername(usernameInputField.getText());
                currentState.setPassword(new String(passwordInputField.getPassword()));
                currentState.setRepeatPassword(new String(repeatPasswordInputField.getPassword()));
                signupViewModel.setState(currentState);
            }

            @Override public void insertUpdate(DocumentEvent e) { documentListenerHelper(); }
            @Override public void removeUpdate(DocumentEvent e) { documentListenerHelper(); }
            @Override public void changedUpdate(DocumentEvent e) { documentListenerHelper(); }
        };

        usernameInputField.getDocument().addDocumentListener(inputFieldListener);
        passwordInputField.getDocument().addDocumentListener(inputFieldListener);
        repeatPasswordInputField.getDocument().addDocumentListener(inputFieldListener);

        this.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) != 0) {
                JRootPane rootPane = SwingUtilities.getRootPane(SignupView.this);
                if (rootPane != null) {
                    rootPane.setDefaultButton(signUp);
                }
            }
        });
        // Action Listeners
        signUp.addActionListener(
            evt -> {
                if (evt.getSource().equals(signUp)) {
                    final SignupState currentState = signupViewModel.getState();
                    if (signupController != null) {
                        signupController.execute(
                            currentState.getUsername(),
                            currentState.getPassword(),
                            currentState.getRepeatPassword()
                        );
                    }
                }
            }
        );

        toLogin.addActionListener(
            evt -> {
                // Original functionality: switch to login view via the controller
                if (signupController != null) {
                    signupController.switchToLoginView();
                }
            }
        );
    }

    /**
     * Original actionPerformed kept as required by the interface.
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        // Keeping original behavior:
        JOptionPane.showMessageDialog(this, "Cancel not implemented yet.");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final SignupState state = (SignupState) evt.getNewValue();
        
        // Error Collapse Logic (Matching LoginView's approach)
        Dimension zeroHeight = new Dimension(Integer.MAX_VALUE, 0);
        
        // If the state has an error, show it via dialog (original behavior) and use the visual fields
        if (state.getUsernameError() != null && !state.getUsernameError().isEmpty()) {
             JOptionPane.showMessageDialog(this, state.getUsernameError());
             // You would normally set text on one of the visual error fields here too
             // e.g., usernameErrorField.setText(state.getUsernameError());
        }
        
        // Visually collapse all unused error fields for tight spacing
        usernameErrorField.setMaximumSize(zeroHeight); 
        usernameErrorField.setVisible(false);
        passwordErrorField.setMaximumSize(zeroHeight);
        passwordErrorField.setVisible(false);
        repeatPasswordErrorField.setMaximumSize(zeroHeight);
        repeatPasswordErrorField.setVisible(false);
    }

    public String getViewName() {
        return viewName;
    }

    public void setSignupController(SignupController controller) {
        this.signupController = controller;
    }
}
