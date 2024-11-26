package ru.nsu.t4werok.towerdefence.app;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.nsu.t4werok.towerdefence.controller.SceneController;
import ru.nsu.t4werok.towerdefence.controller.menu.MainMenuController;
import ru.nsu.t4werok.towerdefence.controller.menu.ReplaySelectionController;
import ru.nsu.t4werok.towerdefence.controller.menu.SettingsController;
import ru.nsu.t4werok.towerdefence.view.menu.ReplaySelectionView;
import ru.nsu.t4werok.towerdefence.view.menu.SettingsView;
import ru.nsu.t4werok.towerdefence.view.menu.MainMenuView;

import static javafx.application.Application.launch;

public class TowerDefenseApplication extends Application {
    @Override
    public void start(Stage stage) {
        SceneController sceneController = new SceneController(stage);

        // Контроллеры
        MainMenuController mainMenuController = new MainMenuController(sceneController);
        SettingsController settingsController = new SettingsController(sceneController);
        ReplaySelectionController replaySelectionController = new ReplaySelectionController(sceneController);

        // Представления
        MainMenuView mainMenuView = new MainMenuView(mainMenuController);
        SettingsView settingsView = new SettingsView(settingsController);
        ReplaySelectionView replaySelectionView = new ReplaySelectionView(replaySelectionController);

        // Регистрация сцен
        sceneController.addScene("MainMenu", mainMenuView.getScene());
        sceneController.addScene("Settings", settingsView.getScene());
        sceneController.addScene("ReplaySelection", replaySelectionView.getScene());

        // Запуск приложения с главного меню
        sceneController.switchTo("MainMenu");

        stage.setTitle("Tower Defence Game");
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}
