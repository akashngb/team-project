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
        super.paintComponent(g);

        if (backgroundImage != null) {
            int panelW = getWidth();
            int panelH = getHeight();

            int imgW = backgroundImage.getWidth(this);
            int imgH = backgroundImage.getHeight(this);

            if (imgW <= 0 || imgH <= 0) {
                return;
            }

            double scale = Math.max(
                    (double) panelW / imgW,
                    (double) panelH / imgH);

            int drawW = (int) (imgW * scale);
            int drawH = (int) (imgH * scale);

            int x = (panelW - drawW) / 2;
            int y = (panelH - drawH) / 2;

            g.drawImage(backgroundImage, x, y, drawW, drawH, this);
        }
    }

    public Image getImage() {
        return this.backgroundImage;
    }

    public void setBackgroundImage(String newBackgroundPath) {
        // FIX: Load the image using the ClassLoader to correctly find resources

        URL imageURL = getClass().getResource(newBackgroundPath);

        if (imageURL != null) {
            this.backgroundImage = new ImageIcon(imageURL).getImage();
        } else {
            System.err.println("Couldn't find background image: " + newBackgroundPath);
            this.backgroundImage = null;
        }

    }
}