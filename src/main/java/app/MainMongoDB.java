package app;

import javax.swing.*;

public class MainMongoDB {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MongoAppBuilder appBuilder = new MongoAppBuilder();
            JFrame application = appBuilder
                    .addLoginView()
                    .addSignupView()
                    .addLoggedInView()
                    .addSignupUseCase()
                    .addLoginUseCase()
                    .addLogoutUseCase()
                    .addChangePasswordUseCase()
                    .build();

            application.pack();
            application.setLocationRelativeTo(null);
            application.setVisible(true);
        });
    }
}
