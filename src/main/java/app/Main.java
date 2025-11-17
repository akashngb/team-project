package app;

import view.FontLoader;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        FontLoader.loadFonts(); // load all fonts in, should be done first
        AppBuilder appBuilder = new AppBuilder();
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
    }
}
