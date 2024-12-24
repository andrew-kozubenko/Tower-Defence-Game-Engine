package ru.nsu.t4werok.towerdefence.controller.game;

import javafx.stage.Stage;
import ru.nsu.t4werok.towerdefence.app.GameEngine;
import ru.nsu.t4werok.towerdefence.config.game.entities.tower.TowerConfig;
import ru.nsu.t4werok.towerdefence.config.game.entities.tower.TowerSelectionConfig;
import ru.nsu.t4werok.towerdefence.config.game.playerState.tech.TechTreeConfig;
import ru.nsu.t4werok.towerdefence.config.game.playerState.tech.TechNodeConfig;
import ru.nsu.t4werok.towerdefence.config.game.playerState.tech.TechTreeSelectionConfig;
import ru.nsu.t4werok.towerdefence.controller.SceneController;
import ru.nsu.t4werok.towerdefence.controller.game.entities.tower.TowerController;
import ru.nsu.t4werok.towerdefence.controller.game.playerState.tech.TechTreeController;
import ru.nsu.t4werok.towerdefence.model.game.entities.enemy.Enemy;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;
import ru.nsu.t4werok.towerdefence.model.game.entities.tower.Tower;
import ru.nsu.t4werok.towerdefence.model.game.playerState.PlayerState;
import ru.nsu.t4werok.towerdefence.model.game.playerState.tech.TechNode;
import ru.nsu.t4werok.towerdefence.model.game.playerState.tech.TechTree;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private final TowerController towerController;
    private final TechTreeController techTreeController;
    private final GameMap gameMap;
    private final List<Tower> towers;
    private final SceneController sceneController;
    private List<TowerConfig> towersForSelect; // Список башен на выбор
    private List<TechTreeConfig> techTreeConfigs;
    private TowerConfig selectedTower = null;
    private final GameEngine gameEngine;
    private PlayerState playerState;
    private List<TechTree> techTrees = new ArrayList<>();

    public List<TowerConfig> getTowersForSelect() {
        return towersForSelect;
    }

    public GameController(GameEngine gameEngine, SceneController sceneController, GameMap gameMap, List<Tower> towers) {
        this.gameEngine = gameEngine;
        this.gameMap = gameMap;
        this.towers = towers;
        this.sceneController = sceneController;
        this.playerState = new PlayerState("Zimbel", 80);
        this.techTreeController = new TechTreeController(techTreeConfigs, playerState);
        this.towerController = new TowerController(gameMap, towers);
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public void setSelectedTower(TowerConfig selectedTower) {
        this.selectedTower = selectedTower;
    }

    public List<TechTree> getTechTrees() {
        return techTrees;
    }

    public void setTowersForSelect(List<TowerConfig> towersForSelect) {
        this.towersForSelect = towersForSelect;
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


    public void updateTower(List<Enemy> enemies) {
        towerController.updateTowers(enemies, playerState);
    }

    public void backToGame(Stage menuStage) {
        menuStage.close();
    }

    public void backToMenu(Stage menuStage) {
        gameEngine.setRunning(false);
        sceneController.switchTo("MainMenu");
        menuStage.close();
    }

    public void loadTowersForSelect() {
        // Загружаем все деревья технологий
        loadTechTrees();

        TowerSelectionConfig towerConfigLoader = new TowerSelectionConfig();
        this.towersForSelect = towerConfigLoader.loadTowers();

        // Для каждой башни находим соответствующее дерево технологий и устанавливаем его
        for (TowerConfig towerConfig : this.towersForSelect) {
            String towerName = towerConfig.getName();  // Имя башни совпадает с именем дерева технологий
            TechTree techTree = techTreeController.findTechTreeByName(towerName);  // Ищем дерево технологий по имени
            if (techTree != null) {
                towerConfig.setTechTree(techTree);  // Устанавливаем найденное дерево технологий в башню
                this.techTrees.add(techTree);
            } else {
                System.out.println("No tech tree found for tower: " + towerName);
            }
        }
    }

    public void loadTechTrees() {
        techTreeController.loadTechTrees();
    }

    public void stop() {
    }

    public void buyUpgrade(TechNode node) {
        techTreeController.buyUpgrade(node);
    }
    public void buyUpgradeForTower(Tower tower, TechNode node) {
        techTreeController.buyUpgradeForTower(tower, node);
    }

    public boolean isUpgradeAvailable(TechNode node) {
        return techTreeController.isUpgradeAvailable(node);
    }


    public TowerConfig getSelectedTower() {
        return selectedTower;
    }

    public boolean checkTowerInCell(int x, int y) {
        for (Tower tower : towers) {
            if (tower.getX() == x && tower.getY() == y) {
                return true;
            }
        }
        return false;
    }

    public Tower getTowerAtCell(int x, int y) {
        for (Tower tower : towers) {
            if (tower.getX() == x && tower.getY() == y) {
                return tower;
            }
        }
        return null;
    }

    public void endCoins() {
        playerState.setCoins(0);
    }
}
