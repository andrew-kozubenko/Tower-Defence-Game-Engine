package ru.nsu.t4werok.towerdefence.controller.game;

import javafx.stage.Stage;
import ru.nsu.t4werok.towerdefence.app.GameEngine;
import ru.nsu.t4werok.towerdefence.config.game.entities.tower.TowerConfig;
import ru.nsu.t4werok.towerdefence.config.game.entities.tower.TowerSelectionConfig;
import ru.nsu.t4werok.towerdefence.config.game.playerState.tech.TechTreeConfig;
import ru.nsu.t4werok.towerdefence.controller.SceneController;
import ru.nsu.t4werok.towerdefence.controller.game.entities.tower.TowerController;
import ru.nsu.t4werok.towerdefence.controller.game.playerState.tech.TechTreeController;
import ru.nsu.t4werok.towerdefence.model.game.entities.enemy.Enemy;
import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;
import ru.nsu.t4werok.towerdefence.model.game.entities.tower.Tower;
import ru.nsu.t4werok.towerdefence.model.game.playerState.PlayerState;
import ru.nsu.t4werok.towerdefence.model.game.playerState.tech.TechNode;
import ru.nsu.t4werok.towerdefence.model.game.playerState.tech.TechTree;
import ru.nsu.t4werok.towerdefence.net.NetworkSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private NetworkSession networkSession;

    /* -------- конструктор -------- */
    public GameController(GameEngine gameEngine,
                          SceneController sceneController,
                          GameMap gameMap,
                          List<Tower> towers) {
        this.gameEngine = gameEngine;
        this.gameMap = gameMap;
        this.towers = towers;
        this.sceneController = sceneController;

        // MVP — один игрок, базовые ресурсы
        this.playerState = new PlayerState("Player", 100);
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
        if (selectedTower == null) {
            return null; // Нет выбранной башни
        }

        int cost = selectedTower.getPrice();
        if (coinsNow() >= cost) {
            // Списываем деньги за башню
            playerState.spendCoins(cost);

            // Добавляем башню через контроллер
            Tower tower = towerController.addTower(selectedTower, x, y);

            // Сбрасываем выбранную башню
//            selectedTower = null;

            return tower;
        } else {
            // Денег недостаточно, башня не ставится
            return null;
        }
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

    public boolean buyUpgrade(TechNode node) {
        return techTreeController.buyUpgrade(node);
    }
    public boolean buyUpgradeForTower(Tower tower, TechNode node) {
        return techTreeController.buyUpgradeForTower(tower, node);
    }

    public boolean isUpgradeAvailable(TechNode node) {
        return techTreeController.isUpgradeAvailable(node);
    }






    /* -------- сетевой слой -------- */
    public void setNetworkSession(NetworkSession session) {
        this.networkSession = session;
    }

    private boolean isMultiplayer() {
        return networkSession != null && networkSession.isConnected();
    }

    /* -------- методы работы с башнями -------- */


    /** Вызывается сетью — без проверок денег/selectedTower. */
    public synchronized void placeTowerRemote(String towerName, int x, int y) {
        Optional<TowerConfig> cfg = findTowerConfigByName(towerName);
        cfg.ifPresent(c -> towerController.addTower(c, x, y));
    }

    private Optional<TowerConfig> findTowerConfigByName(String name) {
        return towersForSelect.stream()
                .filter(c -> c.getName().equals(name))
                .findFirst();
    }

    public TowerConfig getSelectedTower() { return selectedTower; }

    public boolean checkTowerInCell(int x, int y) {
        for (Tower tower : towers) if (tower.getX() == x && tower.getY() == y) return true;
        return false;
    }

    public Tower getTowerAtCell(int x, int y) {
        for (Tower tower : towers) if (tower.getX() == x && tower.getY() == y) return tower;
        return null;
    }

    public int coinsNow() { return playerState.getCoins(); }
}
