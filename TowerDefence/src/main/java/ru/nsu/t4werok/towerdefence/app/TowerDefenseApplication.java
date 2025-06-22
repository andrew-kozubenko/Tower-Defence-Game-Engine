package ru.nsu.t4werok.towerdefence.app;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.nsu.t4werok.towerdefence.controller.SceneController;
import ru.nsu.t4werok.towerdefence.controller.menu.*;
import ru.nsu.t4werok.towerdefence.dev.DevFileInitializer;
import ru.nsu.t4werok.towerdefence.managers.menu.SettingsManager;
import ru.nsu.t4werok.towerdefence.view.menu.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Точка входа Java FX-приложения.
 * MultplayerMenu создаётся после выбора карты,
 * поэтому на старте его не регистрируем.
 */
public class TowerDefenseApplication extends Application {

    @Override
    public void start(Stage stage) {
        /* ---------- singletons ---------- */
        SettingsManager settingsManager = SettingsManager.getInstance();
        settingsManager.setMainStage(stage);

        /* ---------- scene controller ---------- */
        SceneController sceneController = new SceneController(stage);

        /* ---------- controllers ---------- */
        MainMenuController       mainMenuController        = new MainMenuController(sceneController);
        SettingsController       settingsController        = new SettingsController(sceneController);
        ReplaySelectionController replaySelectionController = new ReplaySelectionController(sceneController);
        MapSelectionController   mapSelectionController    = new MapSelectionController(sceneController);

        /* ---------- views ---------- */
        MainMenuView       mainMenuView        = new MainMenuView(mainMenuController);
        SettingsView       settingsView        = new SettingsView(settingsController);
        ReplaySelectionView replaySelectionView = new ReplaySelectionView(replaySelectionController);
        MapSelectionView   mapSelectionView    = new MapSelectionView(mapSelectionController);

        /* ---------- register scenes ---------- */
        sceneController.addScene("MainMenu",        mainMenuView.getScene());
        sceneController.addScene("Settings",        settingsView.getScene());
        sceneController.addScene("ReplaySelection", replaySelectionView.getScene());
        sceneController.addScene("MapSelection",    mapSelectionView.getScene());

        /* ---------- launch ---------- */
        sceneController.switchTo("MainMenu");
        stage.setTitle("Tower Defence Game");
        stage.show();
    }

    public static void main(String[] args) {
        /* ---------- read devMode flag ---------- */
        boolean devMode = false;
        try (InputStream is =
                     TowerDefenseApplication.class.getResourceAsStream("/application.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                devMode = Boolean.parseBoolean(props.getProperty("devMode", "false"));
            } else {
                System.out.println("[WARN] application.properties not found, using devMode=false.");
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Can't load application.properties: " + e.getMessage());
        }

        DevFileInitializer.copyDevFilesIfNeeded(devMode);

        /* ---------- start JavaFX ---------- */
        launch(args);
    }
}
