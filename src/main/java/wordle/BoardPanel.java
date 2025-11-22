package wordle;

import entity.wordle.LetterState;
import interface_adapter.wordle.WordleViewModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Simple board panel that renders up to 6 guesses of 5 letters.
 * Passive: call setViewModel(...) when presenter updates the model.
 */
public class BoardPanel extends JPanel {
    private WordleViewModel vm;

    public BoardPanel() {
        setMaximumSize(getPreferredSize());
    }

    @Override
    public Dimension getPreferredSize() {
        int rows = 6;
        int cols = 5;
        int tile = WordleStyles.TILE_SIZE;
        int pad = 8;
        int width = cols * (tile + pad) + 20;
        int height = rows * (tile + pad) + 20;
        return new Dimension(width, height);
    }


    public void setViewModel(WordleViewModel vm) {
        this.vm = vm;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int rows = 6;
        int cols = 5;
        int pad = 8;
        int tile = WordleStyles.TILE_SIZE;
        int startX = 20;
        int startY = 20;

        // background rectangels and letters
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = startX + c * (tile + pad);
                int y = startY + r * (tile + pad);
                // default tile color
                g.setColor(Color.LIGHT_GRAY);
                g.fillRoundRect(x, y, tile, tile, 8, 8);
                g.setColor(Color.DARK_GRAY);
                g.drawRoundRect(x, y, tile, tile, 8, 8);
            }
        }

        if (vm == null) return;

        List<String> guesses = vm.guessWords;
        List<List<LetterState>> states = vm.guessStates;
        g.setFont(WordleStyles.TILE_FONT);
        FontMetrics fm = g.getFontMetrics();

        for (int r = 0; r < guesses.size() && r < rows; r++) {
            String guess = guesses.get(r);
            List<LetterState> st = states.size() > r ? states.get(r) : null;
            for (int c = 0; c < Math.min(guess.length(), cols); c++) {
                int x = startX + c * (tile + pad);
                int y = startY + r * (tile + pad);
                // color based on correctness state
                if (st != null) {
                    LetterState s = st.get(c);
                    switch (s) {
                        case CORRECT:
                            g.setColor(new Color(106, 170, 100)); // green
                            break;
                        case PRESENT:
                            g.setColor(new Color(201, 180, 88));  // yellow
                            break;
                        default:
                            g.setColor(new Color(120, 124, 126)); // grey
                            break;
                    }

                } else {
                    g.setColor(Color.LIGHT_GRAY);
                }
                g.fillRoundRect(x, y, tile, tile, 8, 8);

                // letter
                g.setColor(Color.WHITE);
                String ch = String.valueOf(guess.charAt(c)).toUpperCase();
                int sw = fm.stringWidth(ch);
                int sh = fm.getAscent();
                g.drawString(ch, x + (tile - sw) / 2, y + (tile + sh) / 2 - 6);
            }
        }
    }
}
