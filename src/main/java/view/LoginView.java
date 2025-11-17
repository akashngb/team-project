package view;

import interface_adapter.login.LoginController;
import interface_adapter.login.LoginState;
import interface_adapter.login.LoginViewModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

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

    private JButton logIn;
    private LoginController loginController = null;

    public LoginView(LoginViewModel loginViewModel) {

        this.loginViewModel = loginViewModel;
        this.loginViewModel.addPropertyChangeListener(this);

        final BufferedImage background = loadBackgroundImage();

        setLayout(new BorderLayout());
        setOpaque(false);

        final BackgroundPanel backgroundPanel = new BackgroundPanel(background);
        backgroundPanel.setLayout(new GridBagLayout());
        add(backgroundPanel, BorderLayout.CENTER);

        final JPanel formPanel = buildFormPanel();
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        backgroundPanel.add(formPanel, constraints);

        configureInputListeners();
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
        passwordInputField.setText(state.getPassword());
    }

    public String getViewName() {
        return viewName;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    private JPanel buildFormPanel() {
        final JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(32, 48, 32, 48));

        final JLabel title = new JLabel("WELCOME BACK");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        title.setForeground(Color.WHITE);

        usernameInputField.setMaximumSize(new Dimension(260, 40));
        passwordInputField.setMaximumSize(new Dimension(260, 40));

        final JPanel usernamePanel = createInputPanel("Username", usernameInputField);
        final JPanel passwordPanel = createInputPanel("Password", passwordInputField);

        usernameErrorField.setForeground(new Color(255, 92, 92));
        usernameErrorField.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordErrorField.setForeground(new Color(255, 92, 92));
        passwordErrorField.setAlignmentX(Component.CENTER_ALIGNMENT);

        final JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.setLayout(new GridLayout(1, 2, 16, 0));
        logIn = buildPrimaryButton("LOG IN");
        JButton cancel = buildSecondaryButton("CANCEL");
        buttons.add(logIn);
        buttons.add(cancel);

        logIn.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent evt) {
                                        if (evt.getSource().equals(logIn)) {
                                            final LoginState currentState = loginViewModel.getState();

                                            loginController.execute(
                                                    currentState.getUsername(),
                                                    currentState.getPassword()
                                            );
                                        }
                                    }
                                }
        );

        cancel.addActionListener(this);


        formPanel.add(title);
        formPanel.add(Box.createVerticalStrut(16));
        formPanel.add(usernamePanel);
        formPanel.add(usernameErrorField);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(passwordPanel);
        formPanel.add(passwordErrorField);
        formPanel.add(Box.createVerticalStrut(24));
        formPanel.add(buttons);

        return formPanel;
    }

    private JPanel createInputPanel(String labelText, JTextField field) {
        final JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        final JLabel label = new JLabel(labelText.toUpperCase());
        label.setForeground(Color.WHITE);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 12f));

        field.setFont(field.getFont().deriveFont(Font.PLAIN, 16f));
        field.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        field.setOpaque(true);
        field.setBackground(new Color(255, 255, 255, 230));

        final JPanel fieldWrapper = new JPanel(new BorderLayout());
        fieldWrapper.setOpaque(false);
        fieldWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 200), 2),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        fieldWrapper.setMaximumSize(new Dimension(260, 48));
        fieldWrapper.add(field, BorderLayout.CENTER);

        panel.add(label);
        panel.add(Box.createVerticalStrut(6));
        panel.add(fieldWrapper);
        return panel;
    }

    private JButton buildPrimaryButton(String text) {
        final JButton button = new JButton(text);
        styleButton(button, new Color(77, 178, 255), Color.WHITE);
        return button;
    }

    private JButton buildSecondaryButton(String text) {
        final JButton button = new JButton(text);
        styleButton(button, new Color(255, 255, 255, 190), new Color(33, 33, 33));
        return button;
    }

    private void styleButton(JButton button, Color background, Color foreground) {
        button.setFocusPainted(false);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setFont(button.getFont().deriveFont(Font.BOLD, 14f));
    }

    private void configureInputListeners() {
        usernameInputField.getDocument().addDocumentListener(new DocumentListener() {

            private void documentListenerHelper() {
                final LoginState currentState = loginViewModel.getState();
                currentState.setUsername(usernameInputField.getText());
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
        });

        passwordInputField.getDocument().addDocumentListener(new DocumentListener() {

            private void documentListenerHelper() {
                final LoginState currentState = loginViewModel.getState();
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
        });

    }

    private BufferedImage loadBackgroundImage() {
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("images/LoginScreenBackground.png")) {
            if (inputStream == null) {
                throw new IllegalStateException("Unable to find LoginScreenBackground.png in resources.");
            }
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load login screen background image.", e);
        }
    }

    private static class BackgroundPanel extends JPanel {
        private final Image background;
        BackgroundPanel(Image background) {
            this.background = Objects.requireNonNull(background);
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }
}