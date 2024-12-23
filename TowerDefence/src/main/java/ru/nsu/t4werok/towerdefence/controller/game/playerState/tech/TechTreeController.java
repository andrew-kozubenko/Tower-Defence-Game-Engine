package ru.nsu.t4werok.towerdefence.controller.game.playerState.tech;

import ru.nsu.t4werok.towerdefence.config.game.playerState.tech.TechNodeConfig;
import ru.nsu.t4werok.towerdefence.config.game.playerState.tech.TechTreeConfig;
import ru.nsu.t4werok.towerdefence.config.game.playerState.tech.TechTreeSelectionConfig;
import ru.nsu.t4werok.towerdefence.model.game.playerState.PlayerState;
import ru.nsu.t4werok.towerdefence.model.game.playerState.tech.TechNode;
import ru.nsu.t4werok.towerdefence.model.game.playerState.tech.TechTree;

import java.util.List;

public class TechTreeController {
    private List<TechTreeConfig> techTreeConfigs;
    private PlayerState playerState;

    public TechTreeController(List<TechTreeConfig> techTreeConfigs,
                              PlayerState playerState) {
        this.techTreeConfigs = techTreeConfigs;
        this.playerState = playerState;
    }

    // Метод для поиска дерева технологий по имени
    public TechTree findTechTreeByName(String towerName) {
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
}
