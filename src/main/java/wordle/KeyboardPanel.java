package wordle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Very simple on-screen keyboard. Key presses will invoke a provided ActionListener.
 * The listener receives action commands like "A", "ENTER", "BACK".
 */
public class KeyboardPanel extends JPanel {
    public KeyboardPanel(ActionListener keyListener) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));


        String[] rows = new String[]{"qwertyuiop", "asdfghjkl", "zxcvbnm"};
        for (int i = 0; i < rows.length; i++) {
            String row = rows[i];
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
            for (char c : row.toCharArray()) {
                JButton b = makeKeyButton(String.valueOf(c).toUpperCase(), keyListener);
                rowPanel.add(b);
            }
            // add backspace at the end of the last row
            if (i == rows.length - 1) {
                JButton back = makeKeyButton("<-", keyListener);
                back.setActionCommand("BACK");
                rowPanel.add(back);
            }
            add(rowPanel);
        }


        /*String[] rows = new String[]{"qwertyuiop", "asdfghjkl", "zxcvbnm"};
        for (String row : rows) {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
            for (char c : row.toCharArray()) {
                JButton b = makeKeyButton(String.valueOf(c).toUpperCase(), keyListener);
                rowPanel.add(b);
            }
            add(rowPanel);
        }

        JPanel control = new JPanel();
        control.setLayout(new BoxLayout(control, BoxLayout.Y_AXIS));

        //JButton enter = makeKeyButton("ENTER", keyListener);
        JButton back = makeKeyButton("BACK", keyListener);

       //control.add(enter);
        control.add(Box.createVerticalStrut(6)); // small gap
        control.add(back);

        add(control);*/
    }

    private JButton makeKeyButton(String label, ActionListener l) {
        JButton b = new JButton(label);
        b.setPreferredSize(new Dimension(48, 36));
        b.setFont(WordleStyles.KEY_FONT);
        b.setFocusable(false);
        b.setActionCommand(label);
        b.addActionListener(l);
        return b;
    }
}
