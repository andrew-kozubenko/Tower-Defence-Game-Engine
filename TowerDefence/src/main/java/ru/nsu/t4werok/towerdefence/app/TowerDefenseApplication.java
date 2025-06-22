package ru.nsu.t4werok.towerdefence.app;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.nsu.t4werok.towerdefence.controller.SceneController;
import ru.nsu.t4werok.towerdefence.controller.menu.MainMenuController;
import ru.nsu.t4werok.towerdefence.controller.menu.MapSelectionController;
import ru.nsu.t4werok.towerdefence.controller.menu.ReplaySelectionController;
import ru.nsu.t4werok.towerdefence.controller.menu.SettingsController;
import ru.nsu.t4werok.towerdefence.dev.DevFileInitializer;
import ru.nsu.t4werok.towerdefence.view.menu.MapSelectionView;
import ru.nsu.t4werok.towerdefence.managers.menu.SettingsManager;
import ru.nsu.t4werok.towerdefence.view.menu.ReplaySelectionView;
import ru.nsu.t4werok.towerdefence.view.menu.SettingsView;
import ru.nsu.t4werok.towerdefence.view.menu.MainMenuView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class TowerDefenseApplication extends Application {
    @Override
    public void start(Stage stage) {
        SettingsManager settingsManager = SettingsManager.getInstance();
        settingsManager.setMainStage(stage);

        SceneController sceneController = new SceneController(stage);

        // Контроллеры
        MainMenuController mainMenuController = new MainMenuController(sceneController);
        SettingsController settingsController = new SettingsController(sceneController);
        ReplaySelectionController replaySelectionController = new ReplaySelectionController(sceneController);
        MapSelectionController mapSelectionController = new MapSelectionController(sceneController, false);

        // Представления
        MainMenuView mainMenuView = new MainMenuView(mainMenuController);
        SettingsView settingsView = new SettingsView(settingsController);
        ReplaySelectionView replaySelectionView = new ReplaySelectionView(replaySelectionController);
        MapSelectionView mapSelectionView = new MapSelectionView(mapSelectionController);

        // Регистрация сцен
        sceneController.addScene("MainMenu", mainMenuView.getScene());
        sceneController.addScene("Settings", settingsView.getScene());
        sceneController.addScene("ReplaySelection", replaySelectionView.getScene());
        sceneController.addScene("MapSelection", mapSelectionView.getScene());

        // Запуск приложения с главного меню
        sceneController.switchTo("MainMenu");

        stage.setTitle("Tower Defence Game");
        stage.show();
    }


    public static void main(String[] args) {
        // 1) Читаем application.properties из ресурсов:
        boolean devMode = false;
        try (InputStream is = TowerDefenseApplication.class.getResourceAsStream("/application.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                String devModeStr = props.getProperty("devMode", "false");
                devMode = Boolean.parseBoolean(devModeStr);
            } else {
                // Файл application.properties не найден, можно вывести предупреждение
                System.out.println("[WARN] application.properties not found, using devMode=false by default.");
            }
        } catch (IOException e) {
            // Ошибка чтения файла
            System.err.println("[ERROR] Can't load application.properties: " + e.getMessage());
        }

        // 2) Перед запуском приложения копируем dev-файлы, если devMode=true
        DevFileInitializer.copyDevFilesIfNeeded(devMode);

        // 3) Запускаем JavaFX
        launch(args);
    }
}
