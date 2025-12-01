package app;

import javax.swing.*;
import view.FontLoader;

public class MainMongoDB {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FontLoader.loadFonts();
            MongoAppBuilder appBuilder = new MongoAppBuilder();
            JFrame application = appBuilder
                    .addLoginView()
                    .addSignupView()
                    .addLoggedInView()
                    .addChessPuzzleView()
                    .addLeaderBoardView()
                    .addSignupUseCase()
                    .addLoginUseCase()
                    .addLogoutUseCase()
                    .addLeaderBoardUseCase()
                    .wireLeaderBoardToLoggedInView()
                    .addWordleFeature()
                    .addChessPuzzleUseCase()
                    .build();

            application.pack();
            application.setLocationRelativeTo(null);
            application.setVisible(true);
        });
    }
}
