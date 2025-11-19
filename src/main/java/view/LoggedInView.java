package view;

import interface_adapter.logged_in.ChangePasswordController;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.logout.LogoutController;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;

public class LoggedInView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "logged in";
    private final LoggedInViewModel loggedInViewModel;
    private final JLabel passwordErrorField = new JLabel();
    private ChangePasswordController changePasswordController = null;
    private LogoutController logoutController;

    private final JLabel username;

    private final JButton logOut;
    private final JTextField passwordInputField = new JTextField(15);
    private final JButton changePassword;

    // ✅ Background image
    private Image backgroundImage;

    public LoggedInView(LoggedInViewModel loggedInViewModel) {

        // ---- Load Background Image ----
        URL bgUrl = getClass().getResource("/images/LoginScreenBackground.png");
        if (bgUrl == null) {
            System.err.println("ERROR: Background image not found at /images/LoginScreenBackground.png");
        } else {
            backgroundImage = new ImageIcon(bgUrl).getImage();
        }
        // --------------------------------

        this.loggedInViewModel = loggedInViewModel;
        this.loggedInViewModel.addPropertyChangeListener(this);

        final JLabel title = new JLabel("Logged In Screen");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(Color.BLACK);

        final LabelTextPanel passwordInfo = new LabelTextPanel(
                new JLabel("Password"), passwordInputField);

        final JLabel usernameInfo = new JLabel("Currently logged in: ");
        usernameInfo.setForeground(Color.BLACK);

        username = new JLabel();
        username.setForeground(Color.BLACK);

        final JPanel buttons = new JPanel();
        buttons.setOpaque(false); // Make buttons panel transparent so BG shows

        logOut = new JButton("Log Out");
        changePassword = new JButton("Change Password");

        buttons.add(logOut);
        buttons.add(changePassword);

        logOut.addActionListener(
                evt -> {
                    if (evt.getSource().equals(logOut)) {
                        logoutController.execute();
                    }
                }
        );

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setOpaque(false);

        passwordInputField.getDocument().addDocumentListener(new DocumentListener() {

            private void documentListenerHelper() {
                final LoggedInState currentState = loggedInViewModel.getState();
                currentState.setPassword(passwordInputField.getText());
                loggedInViewModel.setState(currentState);
            }

            @Override
            public void insertUpdate(DocumentEvent e) { documentListenerHelper(); }

            @Override
            public void removeUpdate(DocumentEvent e) { documentListenerHelper(); }

            @Override
            public void changedUpdate(DocumentEvent e) { documentListenerHelper(); }
        });

        changePassword.addActionListener(
                evt -> {
                    if (evt.getSource().equals(changePassword)) {
                        final LoggedInState currentState = loggedInViewModel.getState();
                        this.changePasswordController.execute(
                                currentState.getUsername(),
                                currentState.getPassword());
                    }
                }
        );

        // Add components
        this.add(Box.createRigidArea(new Dimension(0, 40)));
        this.add(title);
        this.add(Box.createRigidArea(new Dimension(0, 20)));
        this.add(usernameInfo);
        this.add(username);
        this.add(Box.createRigidArea(new Dimension(0, 20)));
        this.add(passwordInfo);
        this.add(passwordErrorField);
        this.add(Box.createRigidArea(new Dimension(0, 20)));
        this.add(buttons);
    }

    /**
     * Draw background image
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public void actionPerformed(ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            final LoggedInState state = (LoggedInState) evt.getNewValue();
            username.setText(state.getUsername());
        }
        else if (evt.getPropertyName().equals("password")) {
            final LoggedInState state = (LoggedInState) evt.getNewValue();
            if (state.getPasswordError() == null) {
                JOptionPane.showMessageDialog(this, "password updated for " + state.getUsername());
                passwordInputField.setText("");
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
