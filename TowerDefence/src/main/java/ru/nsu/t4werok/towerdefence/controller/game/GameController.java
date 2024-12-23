package ru.nsu.t4werok.towerdefence.controller.game;

import javafx.stage.Stage;
import ru.nsu.t4werok.towerdefence.app.GameEngine;
import ru.nsu.t4werok.towerdefence.config.game.entities.tower.TowerConfig;
import ru.nsu.t4werok.towerdefence.controller.SceneController;
import ru.nsu.t4werok.towerdefence.controller.game.entities.tower.TowerController;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;
import ru.nsu.t4werok.towerdefence.model.game.entities.tower.Tower;

import java.util.List;

public class GameController {
    private final TowerController towerController;
    private final GameMap gameMap;
    private final List<Tower> towers;
    private final SceneController sceneController;

    private TowerConfig selectedTower;
    private final GameEngine gameEngine;

    public GameMap getGameMap() {
        return gameMap;
    }

    public GameController(GameEngine gameEngine, SceneController sceneController, GameMap gameMap, List<Tower> towers) {
        this.gameEngine = gameEngine;
        this.gameMap = gameMap;
        this.towers = towers;
        this.towerController = new TowerController(gameMap, towers);
        this.sceneController = sceneController;
    }

    public Tower placeTower(int x, int y) {
        Tower tower = towerController.addTower(selectedTower, x, y);
        selectedTower = null;
        return tower;
    }

    public void selectTower(TowerConfig towerConfig) {
        this.selectedTower = towerConfig;
        System.out.println("Selected tower " + towerConfig.getName());
    }

    public void openSettings(Stage menuStage) {
        sceneController.switchTo("Settings");
        menuStage.close();
    }

    public void backToGame(Stage menuStage) {
        menuStage.close();
    }

    public void backToMenu(Stage menuStage) {
        gameEngine.setRunning(false);
        sceneController.switchTo("MainMenu");
        menuStage.close();
    }

    public void stop() {

    }

    public TowerConfig getSelectedTower() {
        return selectedTower;
    }
}
