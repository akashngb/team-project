package app;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        AppBuilder appBuilder = new AppBuilder();
        JFrame application = appBuilder
                .addLoginView()
                .addSignupView()
                .addLoggedInView()
                .addChessPuzzleView()
                .addSignupUseCase()
                .addLoginUseCase()
                .addLogoutUseCase()
                .addWordleFeature()
                .addChessPuzzleUseCase()
                .build();

        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}