package ru.nsu.t4werok.towerdefence.app;

import javafx.stage.Stage;
import ru.nsu.t4werok.towerdefence.view.menu.MainMenuView;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage primaryStage) {
        MainMenuView mainMenuView = new MainMenuView(primaryStage);
        mainMenuView.show();
    }

    public static void main(String[] args) {
        launch();
    }
}