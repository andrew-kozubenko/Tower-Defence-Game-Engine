package ru.nsu.t4werok.towerdefence.controller.game;

import ru.nsu.t4werok.towerdefence.app.GameEngine;
import ru.nsu.t4werok.towerdefence.controller.game.entities.tower.TowerController;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;
import ru.nsu.t4werok.towerdefence.model.game.entities.tower.Tower;
import ru.nsu.t4werok.towerdefence.view.game.GameView;

import java.util.List;

public class GameController {
    private final TowerController towerController;
    private final GameMap gameMap;
    private final List<Tower> towers;

    public GameController(GameMap gameMap, List<Tower> towers) {
        this.gameMap = gameMap;
        this.towers = towers;
        this.towerController = new TowerController(gameMap, towers);
    }

    public void placeTower(Tower tower) {
        towerController.addTower(tower);
    }

    public List<Tower> loadTowerFromConfigs(String directoryPath) {
        return towerController.loadTowerFromConfigs(directoryPath);
    }
}
