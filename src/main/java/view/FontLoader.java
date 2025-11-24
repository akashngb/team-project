package view;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

public class FontLoader {

    public static Font jersey10;

    public static void loadFonts() {
        // Load the fonts; more fonts can be loaded below
        jersey10 = loadFont("/fonts/Jersey10-Regular.ttf");
    }

    private static Font loadFont(String path) {
        try {
            // 1. Get the font file as an InputStream
            // The leading "/" is crucial for searching from the root of the resources
            InputStream is = FontLoader.class.getResourceAsStream(path);

            if (is == null) {
                throw new IOException("Font file not found at " + path);
            }

            // 2. Create the Font object
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);

            // 3. Register the font with the GraphicsEnvironment
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);

            return font;

        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            // Return a fallback font in case of error
            return new Font("Arial", Font.PLAIN, 12);
        }
    }
}