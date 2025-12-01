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
    }
}