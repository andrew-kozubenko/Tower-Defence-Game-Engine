package ru.nsu.t4werok.towerdefence.controller.menu;

import ru.nsu.t4werok.towerdefence.app.GameEngine;
import ru.nsu.t4werok.towerdefence.config.game.entities.map.MapConfig;
import ru.nsu.t4werok.towerdefence.config.game.entities.map.MapSelectionConfig;
import ru.nsu.t4werok.towerdefence.controller.SceneController;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;
import ru.nsu.t4werok.towerdefence.network.server.GameServer;

import java.util.List;

public class MapSelectionController {
    private final SceneController sceneController;
    private final MapSelectionConfig mapSelectionConfig;
    private final boolean multiplayerHostMode; // true — если мы хостим

    private GameServer gameServer; // сервер, если мы хост

    public MapSelectionController(SceneController sceneController, boolean multiplayerHostMode) {
        this.sceneController = sceneController;
        this.mapSelectionConfig = new MapSelectionConfig();
        this.multiplayerHostMode = multiplayerHostMode;
    }

    public MapSelectionController(SceneController sceneController, boolean multiplayerHostMode,
                                  GameServer gameServer) {
        this.sceneController = sceneController;
        this.mapSelectionConfig = new MapSelectionConfig();
        this.multiplayerHostMode = multiplayerHostMode;
        this.gameServer = gameServer;
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
        if (mapConfig == null) {
            System.out.println("No map selected or map configuration is invalid.");
            return;
        }

        GameMap gameMap = new GameMap(
                mapConfig.getWidth(),
                mapConfig.getHeight(),
                mapConfig.getEnemyPaths(),
                mapConfig.getTowerPositions(),
                mapConfig.getSpawnPoint(),
                mapConfig.getBase(),
                mapConfig.getBackgroundImagePath(),
                mapConfig.getBaseImagePath(),
                mapConfig.getTowerImagePath(),
                mapConfig.getSpawnPointImagePath()
        );

        if (multiplayerHostMode) {
            System.out.println("Starting server and game in host mode...");

            // запускаем GameEngine в сетевом режиме
            new GameEngine(gameMap, sceneController, gameMap.getBase(), true, null, gameServer);
        } else {
            // одиночная игра
            new GameEngine(gameMap, sceneController, gameMap.getBase());
        }
    }
}
