package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

class FadeImagePanel extends ImagePanel implements ActionListener {
    private Image currentImage;
    private Image nextImage;
    private String nextImagePath;
    private final Timer timer;
    private float alpha = 1.0f;
    private final int DURATION = 200;
    private final int STEPS = 20;
    private final int DELAY = DURATION / STEPS;

    public FadeImagePanel(String initialPath) {
        super(initialPath); // Call the original ImagePanel constructor
        this.currentImage = super.getImage();
        this.timer = new Timer(DELAY, this);
        this.timer.setCoalesce(true);
    }

    public void setBackgroundImage(String path) {
        // Load the new image
        URL imageUrl = getClass().getResource(path);
        // ... (error handling) ...
        Image newImage = new ImageIcon(imageUrl).getImage();

        if (this.currentImage != newImage) {
            this.nextImage = newImage;
            this.nextImagePath = path; // STORE THE PATH
            this.alpha = 0.0f;
            this.timer.start();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        // This clears the canvas and draws the image that is fading away.
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (nextImage != null) {
            // Current alpha for fading image
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            // Draw image
            g2d.drawImage(nextImage, 0, 0, getWidth(), getHeight(), this);
            // Subsequent painting operations will immediately draw other components
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }

        // Let the child components (title, buttons, etc.) paint themselves normally
        // This is handled implicitly after paintComponent returns.
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        alpha += 1.0f / STEPS;

        if (alpha >= 1.0f) {
            alpha = 1.0f;
            timer.stop();

            // 1. Permanently update the internal image reference
            this.currentImage = this.nextImage;

            // 2. ***CRITICAL FIX***: Tell the base ImagePanel (super) to load and draw
            // the new image path permanently. This overrides the default background.
            super.setBackgroundImage(this.nextImagePath);

            this.nextImage = null;
            this.nextImagePath = null;
            repaint();
        } else {
            repaint();
        }
    }
}