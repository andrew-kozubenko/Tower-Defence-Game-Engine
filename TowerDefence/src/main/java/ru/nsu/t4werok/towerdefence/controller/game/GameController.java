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
import ru.nsu.t4werok.towerdefence.model.game.entities.map.GameMap;
import ru.nsu.t4werok.towerdefence.model.game.entities.tower.Tower;
import ru.nsu.t4werok.towerdefence.model.game.playerState.PlayerState;
import ru.nsu.t4werok.towerdefence.model.game.playerState.tech.TechNode;
import ru.nsu.t4werok.towerdefence.model.game.playerState.tech.TechTree;

import java.util.List;

public class GameController {
    private final TowerController towerController;
    private final GameMap gameMap;
    private final List<Tower> towers;
    private final SceneController sceneController;
    private List<TowerConfig> towersForSelect; // Список башен на выбор
    private List<TechTreeConfig> techTreeConfigs;
    private TowerConfig selectedTower = null;
    private final GameEngine gameEngine;
    private PlayerState playerState;

    public GameMap getGameMap() {
        return gameMap;
    }

    public List<TowerConfig> getTowersForSelect() {
        return towersForSelect;
    }

    public void setTowersForSelect(List<TowerConfig> towersForSelect) {
        this.towersForSelect = towersForSelect;
    }

    public GameController(GameEngine gameEngine, SceneController sceneController, GameMap gameMap, List<Tower> towers) {
        this.gameEngine = gameEngine;
        this.gameMap = gameMap;
        this.towers = towers;
        this.towerController = new TowerController(gameMap, towers);
        this.sceneController = sceneController;
        this.playerState = new PlayerState("Zimbel", 80);
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

    public void loadTowersForSelect() {
        // Загружаем все деревья технологий
        loadTechTrees();

        TowerSelectionConfig towerConfigLoader = new TowerSelectionConfig();
        this.towersForSelect = towerConfigLoader.loadTowers();

        // Для каждой башни находим соответствующее дерево технологий и устанавливаем его
        for (TowerConfig towerConfig : this.towersForSelect) {
            String towerName = towerConfig.getName();  // Имя башни совпадает с именем дерева технологий
            TechTree techTree = findTechTreeByName(towerName);  // Ищем дерево технологий по имени
            if (techTree != null) {
                towerConfig.setTechTree(techTree);  // Устанавливаем найденное дерево технологий в башню
            } else {
                System.out.println("No tech tree found for tower: " + towerName);
            }
        }
    }

    // Метод для поиска дерева технологий по имени
    private TechTree findTechTreeByName(String towerName) {
        for (TechTreeConfig techTreeConfig : this.techTreeConfigs) {
            if (techTreeConfig.getName().equals(towerName)) {
                // Преобразуем TechTreeConfig в TechTree и возвращаем
                return convertToTechTree(techTreeConfig);
            }
        }
        return null;  // Возвращаем null, если дерево не найдено
    }

    // Преобразование TechTreeConfig в TechTree
    private TechTree convertToTechTree(TechTreeConfig techTreeConfig) {
        TechTree techTree = new TechTree();
        for (TechNodeConfig nodeConfig : techTreeConfig.getRoots()) {
            // Преобразуем каждый TechNodeConfig в TechNode и добавляем его в дерево технологий
            TechNode techNode = new TechNode(nodeConfig.getName(), nodeConfig.getDescription(), nodeConfig.getCost());
            for (TechNodeConfig prerequisiteConfig : nodeConfig.getPrerequisites()) {
                // Преобразуем зависимости в объекты TechNode
                TechNode prerequisiteNode = new TechNode(prerequisiteConfig.getName(), prerequisiteConfig.getDescription(), prerequisiteConfig.getCost());
                techNode.addPrerequisite(prerequisiteNode);
            }
            for (TechNodeConfig childConfig : nodeConfig.getChildren()) {
                // Преобразуем дочерние узлы в объекты TechNode
                TechNode childNode = new TechNode(childConfig.getName(), childConfig.getDescription(), childConfig.getCost());
                techNode.addChild(childNode);
            }
            techTree.addRoot(techNode);  // Добавляем в корни дерева
        }
        techTree.fillPrerequisites();
        return techTree;
    }


    public void loadTechTrees() {
        TechTreeSelectionConfig techTreeSelectionConfig = new TechTreeSelectionConfig();
        this.techTreeConfigs = techTreeSelectionConfig.loadTechTrees();
    }

    public void stop() {

    }

    public void buyUpgrade(TechNode node) {
        // Проверяем доступность улучшения
        if (!isUpgradeAvailable(node)) {
            System.out.println("Upgrade " + node.getName() + " is not available!");
            return;
        }

        // Проверяем, хватает ли у игрока ресурсов на покупку улучшения
        int currentResources = playerState.getCoins(); // Получаем текущие ресурсы игрока
        if (currentResources < node.getCost()) {
            System.out.println("Not enough resources to buy " + node.getName());
            return;
        }

        // Совершаем покупку улучшения
        playerState.setCoins(currentResources - node.getCost()); // Вычитаем стоимость из ресурсов
        node.setUnlocked(true); // Устанавливаем флаг, что улучшение разблокировано

        System.out.println("Upgrade " + node.getName() + " purchased successfully!");
    }

    public boolean isUpgradeAvailable(TechNode node) {
        // Улучшение недоступно, если оно уже куплено
        if (node.isUnlocked()) return false;

        // Улучшение недоступно, если хотя бы одно из требований не выполнено
        for (TechNode prerequisite : node.getPrerequisites()) {
            if (!prerequisite.isUnlocked()) {
                return false;
            }
        }

        // Улучшение доступно
        return true;
    }

    public TowerConfig getSelectedTower() {
        return selectedTower;
    }
}
