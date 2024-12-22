package ru.nsu.t4werok.towerdefence.controller.menu;

import javafx.stage.FileChooser;
import ru.nsu.t4werok.towerdefence.app.GameEngine;
import ru.nsu.t4werok.towerdefence.config.game.entities.map.MapConfig;
import ru.nsu.t4werok.towerdefence.config.game.entities.map.MapSelectionConfig;
import ru.nsu.t4werok.towerdefence.controller.SceneController;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;
import ru.nsu.t4werok.towerdefence.view.game.GameView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class MapSelectionController {
    private final SceneController sceneController;
    private final MapSelectionConfig mapSelectionConfig;

    // Конструктор с передачей сцены
    public MapSelectionController(SceneController sceneController) {
        this.sceneController = sceneController;
        this.mapSelectionConfig = new MapSelectionConfig();
    }

    /**
     * Метод для загрузки всех доступных карт
     */
    public List<MapConfig> getAvailableMaps() {
        return mapSelectionConfig.loadMaps();
    }

    // Метод для обработки нажатия кнопки "Back to Main Menu"
    public void onBackButtonPressed() {
        sceneController.switchTo("MainMenu");
    }

    /**
     * Метод для обработки выбора карты.
     *
     * @param mapConfig Конфигурация выбранной карты.
     */
    public void onMapSelected(MapConfig mapConfig) {
        if (mapConfig != null) {
            // Создание игрового объекта GameMap на основе MapConfig
            GameMap gameMap = new GameMap(
                    mapConfig.getWidth(),
                    mapConfig.getHeight(),
                    mapConfig.getEnemyPaths(),
                    mapConfig.getTowerPositions(),
                    mapConfig.getSpawnPoint(),
                    mapConfig.getBase()
            );

            // Создание игрового движка с картой
            GameEngine gameEngine = new GameEngine(gameMap, sceneController);

            // Запуск игрового движка
            gameEngine.start();
        } else {
            System.out.println("No map selected or map configuration is invalid.");
        }
    }
}
