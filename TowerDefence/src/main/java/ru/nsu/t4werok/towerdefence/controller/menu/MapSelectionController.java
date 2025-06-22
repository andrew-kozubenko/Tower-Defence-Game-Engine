package ru.nsu.t4werok.towerdefence.controller.menu;

import ru.nsu.t4werok.towerdefence.app.GameEngine;
import ru.nsu.t4werok.towerdefence.config.game.entities.map.MapConfig;
import ru.nsu.t4werok.towerdefence.config.game.entities.map.MapSelectionConfig;
import ru.nsu.t4werok.towerdefence.controller.SceneController;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;

import java.util.List;

/**
 * Контроллер экрана выбора карты.
 * <p>– Single-player: при выборе карты создаётся {@link GameEngine}.<br>
 * – Multiplayer: никак не задействован — ведущий открывает отдельное
 * меню “Multiplayer” прямо из главного меню, логика которого уже работает.</p>
 */
public class MapSelectionController {

    private final SceneController   sceneController;
    private final MapSelectionConfig mapSelectionConfig = new MapSelectionConfig();

    public MapSelectionController(SceneController sceneController) {
        this.sceneController = sceneController;
    }

    /* ---------- данные для View ---------- */

    public List<MapConfig> getAvailableMaps() {
        return mapSelectionConfig.loadMaps();
    }

    /* ---------- навигация ---------- */

    public void onBackButtonPressed() {
        sceneController.switchTo("MainMenu");
    }

    /* ---------- выбор карты (single-player) ---------- */

    public void onMapSelected(MapConfig map) {
        if (map == null) {
            System.out.println("No map selected or configuration invalid."); // лог для дебага
            return;
        }

        GameMap gameMap = new GameMap(
                map.getWidth(),
                map.getHeight(),
                map.getEnemyPaths(),
                map.getTowerPositions(),
                map.getSpawnPoint(),
                map.getBase(),
                map.getBackgroundImagePath(),
                map.getBaseImagePath(),
                map.getTowerImagePath(),
                map.getSpawnPointImagePath()
        );

        new GameEngine(gameMap, sceneController, gameMap.getBase());
    }
}
