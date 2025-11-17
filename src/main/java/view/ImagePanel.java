package view;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * A custom JPanel that paints a background image, scaled to
 * fit the panel's full size.
 */
public class ImagePanel extends JPanel {

    private Image backgroundImage;

    public ImagePanel(String imagePath) {
        // Load the image from the resources folder
        try {
            // The leading "/" searches from the root of the resources folder
            URL imageURL = getClass().getResource(imagePath);

            if (imageURL != null) {
                this.backgroundImage = new ImageIcon(imageURL).getImage();
            } else {
                System.err.println("Couldn't find background image: " + imagePath);
                this.backgroundImage = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.backgroundImage = null;
        }
    }

    /**
     * This is the magic method that paints the component.
     */
    @Override
    protected void paintComponent(Graphics g) {
        // Call the superclass method first to paint the panel's base
        super.paintComponent(g);

        // Draw the background image
        if (backgroundImage != null) {
            // This command draws the image, stretching it to fill the
            // entire panel (getWidth() and getHeight())
            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }
}