package wordle;

import interface_adapter.wordle.WordleController;
import interface_adapter.wordle.WordleViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

/**
 * Passive Swing view. UI events are forwarded to controller.
 * The presenter must call setViewModel(vm) via the provided viewUpdater to update the UI.
 */
public class WordleView extends JPanel {
    private final WordleController controller;
    private final BoardPanel boardPanel;
    private final JTextField typingField;
    private final JLabel statusLabel;

    private String userId = "default-user"; // replace with actual logged-in userId integration

    public WordleView(WordleController controller, Consumer<WordleViewModel> vmConsumer) {
        this.controller = controller;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // stack board and bottom vertically

        boardPanel = new BoardPanel();

        JPanel boardContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        boardContainer.add(boardPanel);
        boardContainer.setAlignmentX(Component.CENTER_ALIGNMENT); // center horizontally

        add(Box.createVerticalStrut(20)); // top spacing
        add(boardContainer);
        add(Box.createVerticalStrut(20)); // spacing between board and keyboard

        boardContainer.add(boardPanel);


        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        typingField = new JTextField();
        typingField.setAlignmentX(Component.CENTER_ALIGNMENT);
        typingField.setFont(WordleStyles.KEY_FONT);
        typingField.setHorizontalAlignment(JTextField.CENTER);
        typingField.setColumns(10);


        // Pressing Enter on keyboard submits the guess
        typingField.addActionListener(e -> doSubmit());



        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controls.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton newGame = new JButton("New Game");
        newGame.addActionListener(e -> {
            controller.startNewGame(userId);
            typingField.setText("");
            typingField.requestFocusInWindow();
        });

        controls.add(newGame);

        JButton submit = new JButton("Submit");
        submit.addActionListener(e -> doSubmit());
        controls.add(submit);

        JButton clear = new JButton("Clear");
        clear.addActionListener(e -> typingField.setText(""));
        controls.add(clear);

        bottom.add(typingField, BorderLayout.NORTH);

        KeyboardPanel keyboard = new KeyboardPanel(new KeyListener());
        keyboard.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottom.add(keyboard, BorderLayout.CENTER);
        bottom.add(controls, BorderLayout.SOUTH);

        statusLabel = new JLabel(" ");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

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
        repaint();
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
            if ("ENTER".equals(cmd)) doSubmit();
            else if ("BACK".equals(cmd)) {
                String t = typingField.getText();
                if (!t.isEmpty()) typingField.setText(t.substring(0, t.length() - 1));
            } else {
                // append letter
                typingField.setText(typingField.getText() + cmd.toLowerCase());
            }
        }
    }
}
