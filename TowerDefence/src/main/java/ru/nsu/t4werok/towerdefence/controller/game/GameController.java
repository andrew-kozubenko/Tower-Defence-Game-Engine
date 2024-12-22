package ru.nsu.t4werok.towerdefence.controller.game;

import ru.nsu.t4werok.towerdefence.app.GameEngine;
import ru.nsu.t4werok.towerdefence.config.menu.SettingsConfig;
import ru.nsu.t4werok.towerdefence.controller.SceneController;
import ru.nsu.t4werok.towerdefence.controller.game.entities.tower.TowerController;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;
import ru.nsu.t4werok.towerdefence.model.game.entities.tower.Tower;
import ru.nsu.t4werok.towerdefence.view.game.GameView;

import java.util.List;

public class GameController {
    private final TowerController towerController;
    private final GameMap gameMap;
    private final List<Tower> towers;
    private final SceneController sceneController;

    public GameController(SceneController sceneController, GameMap gameMap, List<Tower> towers) {
        this.gameMap = gameMap;
        this.towers = towers;
        this.towerController = new TowerController(gameMap, towers);
        this.sceneController = sceneController;
    }

    public void placeTower(Tower tower) {
        towerController.addTower(tower);
    }

    public List<Tower> loadTowerFromConfigs(String directoryPath) {
        return towerController.loadTowerFromConfigs(directoryPath);
    }

    public void selectTower(String towerName) {

    }

    public void openSettings() {

    }

    public void goBack() {

    }

    public void stop() {

    }
}
